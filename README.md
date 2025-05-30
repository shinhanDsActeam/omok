# 5 ~ 의 전략


## 개발 환경 설정

### IntelliJ에서 servlet-api.jar 추가하기

1. IntelliJ 메뉴 → `File > Project Structure (⌘ + ;)`
2. 왼쪽에서 `Modules > JSP_MVC_Project` 선택
3. 상단 탭 `Dependencies` 클릭
4. 오른쪽 `+` 버튼 → `JARs or directories` 선택
5. servlet-api.jar 위치를 찾아 선택 (서블릿 설치한거 열면 lib 폴더에 있음)
6. Scope를 꼭 `Provided`로 설정  
   (Tomcat이 실행 시 제공하므로 Provided가 맞음)

---

### 안되면!
inteliJ -> settings -> plugin -> smart tomcat 설치하셈요!  



### 플로우

```mermaid
graph TD
    Start[시작화면] --> Login[회원가입/로그인]
    Login --> |" 아이디 중복체크<br/><br/>닉네임 중복체크<br/><br/>비밀번호 입력 "| Home[홈 화면]
    
    Home --> RoomCreate[방 만들기]
    Home --> RoomCode[방 코드 입력]
    Home --> Mypage[마이페이지]
    
    RoomCreate --> Waiting[대기방 입장 1/2명]
    RoomCode --> Waiting
    
    Waiting --> |"상대방 입장 "| GameStart[게임 시작]
    
    GameStart --> |" 승/패 결정 "| GameEnd[게임 종료]
    
    GameEnd --> |" 다시 대결 "| GameStart
    GameEnd --> |" 나가기 "| Result[결과 화면]
    
    Result --> Home
    
    Mypage --> |" 전적 확인 "| Home

```
### ERD
![image](https://github.com/user-attachments/assets/a5c8ceb1-1914-40ee-a40c-ad5c9444ef70)
