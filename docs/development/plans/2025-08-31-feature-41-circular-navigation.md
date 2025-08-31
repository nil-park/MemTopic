# Development Plan: Circular Navigation System

**브랜치**: `feature/41-circular-navigation`

## 목표

PlayTopicView와 TopicListView 간의 순환 네비게이션 구현 및 AccountView에서 TopicListView로의 일관된 네비게이션 개선

## 작업 스펙

- [x] PlayTopicView에서 백 버튼 시 TopicListView로 이동하도록 수정
- [x] TopicListView에서 백 버튼 시 PlayTopicView로 이동하도록 수정 (최근 재생 토픽이 있는 경우)
- [x] 순환 네비게이션 로직 구현 (PlayTopicView ↔ TopicListView)
- [x] AccountView에서 백 버튼 시 TopicListView로 이동 구현
- [x] 더블 백 투 엑시트는 TopicListView에서만 유지 (500ms 간격으로 개선)

## Claude 지시사항

현재 네비게이션 스택과 BackHandler 수정:

1. PlayTopicView -> TopicListView 순환 네비게이션 구현
   - PlayTopicView에서 백 버튼: TopicListView로 navigate
   - TopicListView에서 백 버튼: 최근 재생 토픽이 있으면 PlayTopicView로 navigate

2. AccountView 백 버튼 처리
   - AccountView에서 백 버튼: TopicListView로 navigate

3. 더블 백 투 엑시트 유지
   - TopicListView에서만 동작
   - 최근 재생 토픽이 없는 경우에만 더블 백으로 앱 종료

## 구현된 기능

### 1. 순환 네비게이션 시스템

- **PlayTopicView → TopicListView**: `popUpTo`를 사용하여 스택 정리
- **TopicListView → PlayTopicView**: 최근 재생 토픽이 있을 때만 이동
- **AccountView → TopicListView**: 스택 정리하며 이동

### 2. 개선된 더블백 시스템

- **500ms 간격**: 더 자연스러운 더블백 감지
- **우선순위 보장**: 더블백 감지 시 순환 네비게이션 취소
- **LaunchedEffect 활용**: 비동기 대기 및 취소 메커니즘

### 3. 네비게이션 로깅

- **실시간 모니터링**: 모든 네비게이션 변경 사항 로그
- **디버깅 지원**: BackHandler 트리거 및 경로 추적
- **스택 변화 추적**: LaunchedEffect로 백스택 변화 감지

## 기술적 구현

- **BackHandler**: `enabled` 조건으로 3개 화면에서 활성화
- **NavController.navigate()**: `popUpTo` 옵션으로 스택 관리
- **State Management**: `pendingNavigation`으로 대기 상태 관리
- **Coroutines**: `kotlinx.coroutines.delay`로 타이밍 제어

## 진행 상황

- ✅ 계획 수립 완료
- ✅ 순환 네비게이션 구현 완료
- ✅ 더블백 시스템 개선 완료
- ✅ 네비게이션 로깅 구현 완료
- ✅ 테스트 및 검증 완료
- ✅ feature/41-circular-navigation 브랜치 푸시 완료

## 알려진 이슈

- [#52](https://github.com/nil-park/MemTopic/issues/52): PlayTopicView 백 버튼이 간헐적으로 BackHandler를 우회하는 문제
