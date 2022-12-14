#!/usr/bin/env bash
ABSPATH=$(readlink -f $0)
ABSDIR=$(dirname $ABSPATH)
source ${ABSDIR}/profile.sh

REPOSITORY=/home/ubuntu/app/step/zip
PROJECT_NAME=Tiger

echo "> Build 파일 복사"

cp $REPOSITORY/*.jar $REPOSITORY/

#echo "> 현재 구동중인 애플리케이션 pid 확인"
#
#CURRENT_PID=$(pgrep -fl Tiger | grep java | awk '{print $1}')

#echo "현재 구동중인 어플리케이션 pid: $CURRENT_PID"
#
#if [ -z "$CURRENT_PID" ]; then
#    echo "> 현재 구동중인 애플리케이션이 없으므로 종료하지 않습니다."
#else
#    echo "> kill -15 $CURRENT_PID"
#    kill -15 $CURRENT_PID
#    sleep 5
#fi

echo "> 새 어플리케이션 배포"

JAR_NAME=$(ls -tr $REPOSITORY/*.jar | tail -n 1)

echo "> JAR Name: $JAR_NAME"


echo "> $JAR_NAME 실행"

IDLE_PROFILE=$(find_idle_profile)
echo "> $JAR_NAME 를 profile=$IDLE_PROFILE 로 실행합니다."

echo "cd $REPOSITORY"

cd $REPOSITORY || exit


echo "nohup java -Duser.timezone=Asia/Seoul -jar \
              -Dspring.profiles.active=$IDLE_PROFILE \
              $JAR_NAME > $REPOSITORY/nohup.out 2>&1 &"
#nohup java -jar $JAR_NAME > $REPOSITORY/nohup.out 2>&1 &
nohup java -Duser.timezone=Asia/Seoul -jar \
        -Dspring.profiles.active=$IDLE_PROFILE\
        $JAR_NAME > $REPOSITORY/nohup.out 2>&1 &

