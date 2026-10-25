# Java Decompilation Experiments

Java 컴파일 과정에서 발생하는 소스코드 정보 손실과 디컴파일을 통한 코드 복원 가능성을 분석하는 실험 프로젝트입니다.

동일한 Java 소스코드를 서로 다른 컴파일 옵션으로 컴파일하고, CFR 디컴파일러로 복원한 결과를 비교합니다. 또한 복원된 코드를 다시 컴파일하여 원본과의 동작 일치 여부를 검증합니다.

## 1. 실험 목적

본 프로젝트는 다음 세 가지 질문을 중심으로 진행합니다.

1. Java 컴파일 과정에서 원본 소스코드의 어떤 정보가 유지되거나 손실되는가?
2. 디컴파일러는 컴파일된 바이트코드로부터 원본 코드의 구조를 어느 정도 재구성할 수 있는가?
3. 디컴파일된 코드가 원본 코드와 동일하게 동작하는가?

여기서 **원본 소스코드의 정확한 복원**과 **동작이 동등한 코드의 재구성**은 구분합니다.

## 2. 실험 환경

| 항목 | 도구 |
|---|---|
| Language | Java |
| JDK / Compiler | 25.0.4.1 |
| Decompiler | CFR-0.151 |
| Bytecode Analysis | javap |
| IDE | IntelliJ IDEA |

## 3. 실험 대상

**RateLimiter.java**

API 요청 횟수를 제한하는 Fixed Window Rate Limiter를 Java로 구현했습니다.

- 사용자별 요청 횟수를 `HashMap`으로 관리
- 10초 동안 최대 3회의 요청 허용
- 제한 초과 시 `false` 반환
- 제한 시간이 경과하면 요청 횟수 초기화

외부 라이브러리 없이 Java 표준 라이브러리만 사용합니다.

## 4. 실험 구성

### Experiment 01 — Debug Information

`javac -g`와 `javac -g:none`으로 동일한 소스코드를 컴파일한 뒤, `javap -v`를 이용해 클래스 파일의 정보를 비교합니다.

주요 분석 대상은 `LocalVariableTable`, `LineNumberTable` 등 디버그 관련 속성입니다.

### Experiment 02 — Source Reconstruction

두 클래스 파일을 CFR로 디컴파일한 후 원본 소스코드와 비교합니다.

변수명, 타입, 조건문, 반복문, 내부 클래스 구조 등 소스코드가 어떻게 재구성되는지 분석합니다.

### Experiment 03 — Behavioral Comparison

디컴파일된 코드를 다시 컴파일하고 원본 프로그램과 동일한 입력을 전달하여 실행 결과를 비교합니다.

실행 결과의 일치는 테스트한 입력에 대한 동작 일치를 의미하며, 모든 가능한 입력에 대한 동등성을 증명하지는 않습니다.

## 5. 프로젝트 구조

```text
java-decompilation-experiments/
├── src/
│   └── RateLimiter.java
├── build/
│   ├── debug/
│   └── nodebug/
├── output/
│   ├── debug/
│   │   └── RateLimiter.java
│   └── nodebug/
│       └── RateLimiter.java
├── results/
│   └── README.md
└── README.md
```

`build/`에는 컴파일된 클래스 파일을, `output/`에는 디컴파일 결과를, `results/`에는 실험 결과 및 분석 내용을 보관합니다.

## 6. 실험 재현

프로젝트 루트에서 다음 명령어를 실행합니다.

**컴파일**

```bash
javac -g -d build/debug src/RateLimiter.java
javac -g:none -d build/nodebug src/RateLimiter.java
```

**바이트코드 정보 확인**

```bash
javap -v build/debug/RateLimiter.class
javap -v build/nodebug/RateLimiter.class
```

**CFR 디컴파일**

CFR JAR 파일을 `tools/cfr.jar`에 배치한 경우:

```bash
java -jar tools/cfr.jar build/debug/RateLimiter.class --outputdir output/debug
java -jar tools/cfr.jar build/nodebug/RateLimiter.class --outputdir output/nodebug
```

**복원 코드 재컴파일 및 실행**

```bash
javac -d build/recompiled-debug output/debug/RateLimiter.java
javac -d build/recompiled-nodebug output/nodebug/RateLimiter.java

java -cp build/debug RateLimiter
java -cp build/recompiled-debug RateLimiter
java -cp build/recompiled-nodebug RateLimiter
```

## 7. 실험 결과

각 실험의 결과와 분석은 `results/` 디렉터리에 정리합니다.

- Experiment 01: 디버그 정보 손실 분석
- Experiment 02: 원본 및 디컴파일 코드 비교
- Experiment 03: 실행 결과 비교

## 8. 참고 자료

- [CFR Java Decompiler](https://www.benf.org/other/cfr/)
- [Java Virtual Machine Specification](https://docs.oracle.com/javase/specs/jvms/se21/html/)
- [javap Documentation](https://docs.oracle.com/en/java/javase/21/docs/specs/man/javap.html)
