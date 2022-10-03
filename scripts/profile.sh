#!/usr/bin/env bash

# bash는 return value가 안되니 *제일 마지막줄에 echo로 해서 결과 출력*후, 클라이언트에서 값을 사용한다

# 쉬고 있는 profile 찾기: port1이 사용중이면 port2가 쉬고 있고, 반대면 port1이 쉬고 있음
function find_idle_profile()
{
    RESPONSE_CODE=$(curl -s -o /dev/null -w "%{http_code}" http://52.79.109.52/profile)

    if [ ${RESPONSE_CODE} -ge 400 ] # 400 보다 크면 (즉, 40x/50x 에러 모두 포함)
    then
        CURRENT_PROFILE=port2 # 에러 발생 시 현재 profile을 real2로 사용
    else
        CURRENT_PROFILE=$(curl -s http://52.79.109.52/profile)
    fi

    if [ ${CURRENT_PROFILE} == port1 ]
    then
      IDLE_PROFILE=port2
    else
      IDLE_PROFILE=port1
    fi

    echo "${IDLE_PROFILE}"
}

# 쉬고 있는 profile의 port 찾기
function find_idle_port()
{
    IDLE_PROFILE=$(find_idle_profile)

    if [ ${IDLE_PROFILE} == port1 ]
    then
      echo "8081"
    else
      echo "8082"
    fi
}