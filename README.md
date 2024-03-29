# 개인 프로젝트
## 개인 프로젝트 소개
* 팀 프로젝트를 진행하며 해보지 못해 아쉬웠던 부분을 직접 해보며 공부하기 위해 이 프로젝트를 시작했습니다.
* 이 프로젝트의 목적은 아래와 같은 부분을 공부 해보기 위함으로, 기능 자체는 아주 단순한 커뮤니티 사이트 입니다.
1. Spring Security 적용
2. AWS를 이용한 배포
3. Test Code 작성

### 1. Spring Security 적용
* Spring Security를 적용하고, JWT와 OAuth2를 구현하였습니다.
* OAuth2의 경우 google만 구현하였으며, 자체 회원가입은 없고 google 로그인을 통한 회원가입만 가능합니다.
* google 로그인을 통해 회원 가입 후 추가적으로 필요한 정보(닉네임, 휴대폰 번호)는 회원 수정 기능을 통해 수정할 수 있도록 하였습니다.
  * 글, 댓글 작성자는 닉네임으로 표기가 되며, google 로그인을 통해 회원가입만 하면 닉네임이 없는상황입니다.
  * 글, 댓글 작성시 닉네임이 없으면 작성할 수 없도록 구현하였습니다.
* JWT를 이용하여, 로그인 시 액세스 토큰과 리프레시 토큰을 발급받도록 구현하였습니다.
* 또한 액세스 토큰 만료 시 리프레시 토큰을 통해 다시 액세스 토큰을 재발급 받을 수 있도록 구현하였습니다.
* 또한 Security 설정을 통해 접근권한이 필요한 부분과 필요 없는 부분을 구분해 놓았습니다.

### 2. AWS를 이용한 배포
* AWS EC2, AWS RDS를 이용해 최초 배포를 시작했습니다.
* 추가로 Github actions, AWS S3, AWS CodeDeploy을 이용하여 CI/CD를 구현해 배포 자동화를 구현하였습니다.
* 그 이후 Nginx(Reverse Proxy 기능)를 이용하여, EC2에 Nginx 1대와 Spring boot 2대를 사용하여 서버 중단 없이 배포를 할 수 있도록 무중단 배포를 구현하였습니다.
  * Spring boot1은 8081포트로 실행합니다.
  * Spring boot2는 8082포트로 실행합니다.
  * 만약 nginx가 spring boot1과 연결되어 있고 신규 배포가 필요한 상황이면, spring boot2로 배포를 진행 한 후 nginx가 spring boot2를 바라보도록 reload하면 됩니다.

![배포 flow](https://user-images.githubusercontent.com/104135638/212538388-8374a96b-9b42-48e9-bf1e-bd8591e08e3d.PNG)


### 3. Test Code 작성
* 테스트 코드에 익숙해지기 위해 Spring REST Docs를 이용하여 테스트기반의 REST API 문서화를 진행 하였습니다.
* 단위테스트들을 진행하여 앱의 안정성을 높였습니다.
