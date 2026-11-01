#!/usr/bin/env bash
# [챌린지] 같은 값으로 동시에 요청을 보내, 사전 중복 체크를 함께 통과한 요청이
# DB UNIQUE 제약에서 막히는 상황을 재현한다.
# 사용법: bash scripts/race_test.sh [동시 요청 수=20]
N=${1:-20}
BASE=${BASE:-http://localhost:8080}
TAG=$(date +%s)
OUT=$(mktemp -d)

fire() { # $1=이름, $2=URL, $3..=요청 본문 목록(파일)
  local name=$1 url=$2; shift 2
  local i=0
  for body in "$@"; do
    i=$((i + 1))
    curl -s -o "$OUT/$name-$i.json" -w '%{http_code}\n' -X POST "$url" \
      -H 'Content-Type: application/json' --data-binary @"$body" > "$OUT/$name-$i.status" &
  done
  wait
  echo "== $name: 동시 요청 ${#@}건"
  paste -d' ' <(cat "$OUT"/$name-*.status) <(for f in "$OUT"/$name-*.json; do grep -o '"code":"[A-Z0-9]*"' "$f"; done) \
    | sort | uniq -c
}

# 1) 같은 이메일, 서로 다른 닉네임
bodies=()
for i in $(seq 1 "$N"); do
  f="$OUT/email-body-$i"
  printf '{"email":"race%s@example.com","password":"Umc!1234","nickname":"ra%s_%s"}' "$TAG" "${TAG: -4}" "$i" > "$f"
  bodies+=("$f")
done
fire email "$BASE/members" "${bodies[@]}"

# 2) 같은 닉네임, 서로 다른 이메일
bodies=()
for i in $(seq 1 "$N"); do
  f="$OUT/nick-body-$i"
  printf '{"email":"nick%s_%s@example.com","password":"Umc!1234","nickname":"nk%s"}' "$TAG" "$i" "${TAG: -6}" > "$f"
  bodies+=("$f")
done
fire nickname "$BASE/members" "${bodies[@]}"

# 3) 같은 회원(2번)이 같은 영화(movieId = TAG)에 동시에 평점 등록
bodies=()
for i in $(seq 1 "$N"); do
  f="$OUT/rating-body-$i"
  printf '{"movieId":%s,"score":4,"comment":"동시 요청 %s"}' "$TAG" "$i" > "$f"
  bodies+=("$f")
done
fire rating "$BASE/members/2/ratings" "${bodies[@]}"

rm -rf "$OUT"
