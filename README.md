# MARU_FIRM

MARU_FIRM는 Java 기반의 펌뱅킹 서비스를 제공하는 경량 서버 프로세스(`com.pgmate.firm.server.Server`)입니다. 저장소에는 서비스 실행을 위한 쉘 스크립트, 데이터베이스 및 은행 연동 설정 파일, 번들 의존성 라이브러리가 포함되어 있습니다.

## 프로젝트 구성

- `bin/` – 서버 구동 및 관리용 유틸리티(`start.sh`, `stop.sh`, `monitor.sh`)와 Ant 빌드 파일(`build.xml`).
- `conf/` – 데이터베이스 연결(`service.json`), 펌/은행 설정(`firm.json`), 로깅(`logback.xml`), 메시지 프로퍼티 등 각종 설정 파일.
- `lib/` – 실행 및 빌드에 필요한 서드파티 JAR.
- `src/` – 서버 구현 소스 트리(이 스냅샷에는 포함되지 않음).
- `README.txt` – 기존의 간단한 저장소 설명.

## 빌드 방법

Ant 스크립트를 사용해 `src/` 아래의 Java 소스를 컴파일하고 `lib/MARU_firm.jar`로 패키징합니다.

```sh
cd bin
ant -f build.xml compile
```

컴파일 시 `lib/`의 JAR을 클래스패스에 추가하고 결과 클래스 파일을 `classes/` 디렉터리에 생성합니다.

## 실행 방법

`bin/` 디렉터리에서 제공하는 스크립트를 사용합니다.

- `start.sh`: 번들된 JAR로 클래스패스를 구성하고 로케일을 설정한 뒤 서버를 백그라운드로 실행합니다.
- `stop.sh`: `firm.pid`에 기록된 프로세스를 중지합니다.
- `monitor.sh`: 프로세스 상태를 간단히 확인합니다.

서버가 설정을 읽을 수 있도록 스크립트 위치를 기준으로 `conf/` 디렉터리가 함께 존재해야 합니다.

## 설정 안내

- `conf/service.json`: 데이터베이스 JDBC URL, 계정 정보, 커넥션 풀 크기 등을 정의합니다.
- `conf/firm.json`: 은행 코드, 계좌, API 키, 런타임 포트 등 서비스 운영에 필요한 펌/은행별 설정을 포함합니다.
- `conf/logback.xml`: 서버 기동 시 사용되는 로깅 설정을 제공합니다.

배포 또는 실행 전에 샘플 자격 증명과 키를 실제 환경에 맞는 값으로 교체하세요.