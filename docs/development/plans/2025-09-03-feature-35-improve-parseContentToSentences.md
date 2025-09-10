# Development Plan: Improve parseContentToSentences Function

**브랜치**: `feature/35-improve-parseContentToSentences-unicode-support`
**이슈**: [#35](https://github.com/nil-park/MemTopic/issues/35)

## 목표

현재 단순한 문장 분할 로직을 Java BreakIterator로 교체하여 다국어 문장 분할 정확도를 크게 향상시킨다.

## 현재 문제점 (ContentParser.kt:4-28)

```kotlin
// 현재 구현의 한계들:
"Dr. Smith went home." → ["Dr.", " Smith went home."] ❌
"I have 2.5 dollars." → ["I have 2.", "5 dollars."] ❌  
"안녕하세요。 테스트입니다。" → [전체가 하나의 문장] ❌
```

## 해결 방법: BreakIterator 사용

Java/Android 내장 국제화 텍스트 분할 API 활용:

- 축약어 자동 인식 (Dr., Mr., U.S.A.)
- 소수점 보호 (2.5, 3.14)
- 다국어 구두점 지원
- 유니코드 공백 처리

## 작업 스펙

- [x] 현재 함수 분석 완료 (`ContentParser.kt`)
- [x] BreakIterator 기반 새 구현 작성
- [x] 기존 실패 케이스 테스트 작성
- [x] 새 구현으로 테스트 통과 확인
- [x] 기존 호출부와 호환성 검증

## 예상 결과

```kotlin
// 개선 후 예상 결과들:
"Dr. Smith went home." → ["Dr. Smith went home."] ✅
"I have 2.5 dollars." → ["I have 2.5 dollars."] ✅
"안녕하세요. 테스트입니다!" → ["안녕하세요.", "테스트입니다!"] ✅
"First sentence.\n\nSecond sentence." → ["First sentence.", "Second sentence."] ✅
```

## 구현 범위

아래 파일들만 수정할 것

- `android/app/src/main/java/com/nolbee/memtopic/utils/ContentParser.kt`
- `android/app/src/test/java/com/nolbee/memtopic/utils/ContentParserTest.kt`
- `docs/development/plans/2025-09-03-feature-35-improve-parseContentToSentences.md`

## 상태

### commit ac096ea3

- 다음 라인 전체를 하나의 발화 단위로 인식했으면 좋겠는데, 그렇게 안됨.

  ```plaintext
  "Oh no, How will I chop wood without my axe?" he cried.
  ```

  - Desired: `"Oh no, How will I chop wood without my axe?" he cried.`
  - Got: `"Oh no, How will I chop wood without my axe?"`, `he cried.`

- 스페이스로 나뉘어진 문단은 분리했으면 좋겠는데, 하나로 보임.

  ```plaintext
  The spirit became furious, "You lier, I'll teach you a lesson"
  A splash of water burst from the lake and drenched Chilseong.
  ```

  - Desired: `The spirit became furious, "You lier, I'll teach you a lesson"`, `A splash of water burst from the lake and drenched Chilseong.`
  - Got: `The spirit became furious, "You lier, I'll teach you a lesson" A splash of water burst from the lake and drenched Chilseong.`

### commit 84311640

- 위 두 케이스는 커버됨.

- 다음 라인은 테스트 실패 반복됨. 이런 예외까지 처리하기에는 룰 베이스 코드로는 너무 과하다고 판단해서 주석 처리함.

  ```plaintext
  Mr. and Mrs. Smith live in U.S.A. They moved from U.K.
  ```

  - Desired: `Mr. and Mrs. Smith live in U.S.A.`, `They moved from U.K.`
  - Got: `Mr. and Mrs. Smith live in U.S.A. They moved from U.K.`

- `ContentParser.kt` 파일의 `parseWithQuotedTextHandling` 함수가 너무 긴 것 같다. 주석은 줄이고 대신 함수를 나눠서 함수 이름으로 기능을 확인할 수 있게 하고, 유닛 테스트가 가능하게 하자.

### commit ccf617b1

- 리팩토링 완료
- 구두점이 없는 문장에 꼭 구두점을 넣을 필요는 없을 것 같은데?

### commit 4e20da5

- 자동 마침표 추가 로직 제거하여 원문을 나누기만 하고 없는 것을 넣지 않게 변경
