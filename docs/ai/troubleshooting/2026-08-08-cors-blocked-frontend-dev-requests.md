# 로컬 프론트엔드(Vite dev server)에서 백엔드 API 호출이 브라우저에서 전부 막힘

- 날짜: 2026-08-08
- 관련 커밋: `364ba6c` Allow the local frontend dev origin to call the API (CORS)

## 증상

프론트엔드 화면을 실제 브라우저(Chrome)에서 살아있는 백엔드와 붙여 수동으로 검증하던 중, Vite dev server(`localhost:5173`)에서 백엔드(`localhost:8080`)로 보내는 모든 요청이 브라우저에서 차단됨.

## 원인

Spring MVC는 기본적으로 CORS 정책이 없다 (모든 크로스 오리진 요청을 거부). 백엔드에 CORS 설정을 명시적으로 추가한 적이 없었기 때문에 발생.

## 해결

`config/WebConfig.java`에 `WebMvcConfigurer`를 구현해 `/api/**` 경로에 대해 로컬 프론트엔드 dev 오리진을 허용하는 CORS 매핑을 추가했다.

```java
@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins("http://localhost:5173")
                .allowedMethods("GET", "POST", "PUT", "DELETE");
    }
}
```

## 재발 방지를 위해 확인할 것

- 새 프론트엔드 dev 오리진(포트 변경 등)이 생기면 `WebConfig.allowedOrigins`에 추가해야 한다.
- 배포 환경의 실제 프론트엔드 도메인은 아직 이 설정에 반영되어 있지 않다 — 배포 구성을 다룰 때 반드시 확인.
- 프론트엔드 화면 작업은 코드만 보고 끝내지 말고, 실제로 살아있는 백엔드에 붙여 브라우저에서 확인해야 이런 문제를 조기에 발견한다.

## 관련 문서

- 없음 (CORS 설정은 별도 규칙 문서로 다룰 만큼 반복되는 주제가 아직 아님).
