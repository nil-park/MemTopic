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
- [ ] BreakIterator 기반 새 구현 작성
- [ ] 기존 실패 케이스 테스트 작성
- [ ] 새 구현으로 테스트 통과 확인
- [ ] 기존 호출부와 호환성 검증

## 예상 결과

```kotlin
// 개선 후 예상 결과들:
"Dr. Smith went home." → ["Dr. Smith went home."] ✅
"I have 2.5 dollars." → ["I have 2.5 dollars."] ✅
"안녕하세요. 테스트입니다!" → ["안녕하세요.", "테스트입니다!"] ✅
"First sentence.\n\nSecond sentence." → ["First sentence.", "Second sentence."] ✅
```

## 구현 방향

```kotlin
object ContentParser {
    fun parseContentToSentences(content: String): List<String> {
        val iterator = BreakIterator.getSentenceInstance()
        iterator.setText(content.replace(Regex("\\s+"), " ").trim())
        
        val sentences = mutableListOf<String>()
        var start = iterator.first()
        
        while (start != BreakIterator.DONE) {
            val end = iterator.next()
            if (end != BreakIterator.DONE) {
                val sentence = content.substring(start, end).trim()
                if (sentence.isNotBlank()) sentences.add(sentence)
            }
            start = end
        }
        
        return sentences
    }
}
```

## 진행 상황

- ✅ 브랜치 생성 완료
- ✅ 현재 구현 분석 완료 (`ContentParser.kt:4-28`)
- ✅ 기술적 방향성 결정 (BreakIterator)
- ✅ Draft PR 생성 완료 (#54)
- ✅ BreakIterator 기반 새 구현 완료
- ✅ 포괄적 테스트 케이스 작성 완료
- ✅ 코드 커밋 완료 (983504e)
- [ ] 테스트 실행 및 검증 (Java 환경 필요)
- [ ] 기존 코드와 호환성 확인
- [ ] PR Ready for Review 전환

## 구현 완료 내용

### 1. ContentParser.kt 개선
- **Before**: 단순한 `.?!` 문자 기반 분할 + 개행 처리
- **After**: BreakIterator + 공백 정규화 + 빈 문장 필터링

### 2. 테스트 케이스 (ContentParserTest.kt)
- 12개 테스트 케이스 작성
- 빈 문자열, 축약어, 소수점, 한국어, 혼합언어, 유니코드 구두점 등 커버

### 3. 커밋 상태
- 브랜치: `feature/35-improve-parseContentToSentences-unicode-support`
- 최신 커밋: `983504e` (미푸시 상태)
- Draft PR: https://github.com/nil-park/MemTopic/pull/54

## 다음 작업 (재개 시)

1. `git push` - 최신 구현 푸시
2. Java 환경 설정 후 테스트 실행
3. 실제 앱에서 동작 검증
4. Draft PR을 Ready for Review로 전환
5. 이슈 #35 완료

## 상태

**⏸️ 작업 중단** - 핵심 구현 완료, 테스트 검증 남음 (2025-09-03)
