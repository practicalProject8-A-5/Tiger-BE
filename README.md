</div

![header](https://capsule-render.vercel.app/api?type=waving&text=ta,iger&color=auto&height=200&align=center&animation=scaleIn)
<br>
# C2C 카 쉐어링 중개 플랫폼 - <a href="https://taiger.kr/">타, 이거</a>

## 프로젝트 기간
- 2022.08.26 ~ 2022.10.07

## 팀구성
이름 | 깃허브 주소 | 포지션 
--- | --- | --- 
손성우 | https://github.com/sungkong | Back-End
정윤혁 | https://github.com/tanibourne | Back-End 
최준우 | https://github.com/gitpher | Back-End 
심채운 | https://github.com/Sim0321 | Front-End
권익현  | https://github.com/kwonih1020 | Front-End
허지연 | yeooon02@gmail.com | UI/UX 

## 팀 노션 페이지
<a href="https://www.notion.so/ta-iger-c2c-4b3b2ff06b23444d9c6154a6ae8d638c">노션페이지 바로가기</a>

## 와이어프레임 & 아키텍쳐 
![서비스 아키텍처 (수정본)](https://user-images.githubusercontent.com/26310384/194273279-5e5fd8c8-db24-43c0-959b-16cf517a4f97.png)

## ERD
![ERD (1)](https://user-images.githubusercontent.com/73283404/194310791-32d872c1-6494-480f-90d2-c2922a8ba9bd.png)

## 🛠 개발환경
- SpringBoot 2.7.3
- MySql 8.0.28
- Gradle 7.4
- AWS EC2
- AWS RDS
- AWS Route53
- AWS CodeDeploy
- TravisCI
- Nginx
- WebSocket
- STOMP

## ❗ 맡은 역할
### 1. 원하는 기간 예약 및 결제 💬 

![image](https://user-images.githubusercontent.com/73283404/194958715-33b2bf14-c09e-4b0a-ab42-d42a1760c187.png)

### 2. 주문관리📅 

![image](https://user-images.githubusercontent.com/73283404/194963833-d686649a-9b14-42b4-b6b4-4b0dd307da57.png)

![Screen Shot 2022-10-03 at 2 11 03 PM](https://user-images.githubusercontent.com/26310384/194005029-7278bcea-5dca-4206-b11b-faf3ca84cd19.png)

### 3. xss 보안 🛠

![image](https://user-images.githubusercontent.com/73283404/194964280-2e67137d-7783-4330-9361-13d265f0968b.png)
![image](https://user-images.githubusercontent.com/73283404/194964550-f59cbfd4-01dc-45ac-8c67-203d1a9f7f58.png)

### 4. 서버 테스트 🛠
● 목적 : 실제 서비스 운영시 서버가 허용할 수 있는 최대 사용자 수를 구하고 이를 기준으로 성능 최적화를 구분할 수 있는 지표 만들기
<br>● 테스트 시나리오
1) Failure값이 여러 개 존재하거나(1~2개는 오차의 범위), 급격히 튀는 값이 존재하면 실패라고 간주
2) 응답시간이 평균 응답시간대 보다 월등히 길어지는 경우 실패로 간주
3) 런칭후 구글 애널리틱스 기준으로 트래픽이 가장 많이 일어나는 메인페이지와 검색 2가지 경우만 테스트
4) 쓰레드를 100으로 시작하여 점진적으로 100씩 증가시킨다.

![image](https://user-images.githubusercontent.com/73283404/194965354-66da5f37-2397-41f2-aa5c-a571f0649475.png)
[Start Threads Count 1600 [실패]
![image](https://user-images.githubusercontent.com/73283404/194965398-ddef3a2a-106c-449a-a367-8c567887e440.png)
[Responses Times Over Time]

![image](https://user-images.githubusercontent.com/73283404/194965512-364d4dea-f0a0-430d-a5f6-ebde4af92535.png)


## 📹 발표영상
<a href="https://youtu.be/DNwxfcv-KXw">영상보기</a>

