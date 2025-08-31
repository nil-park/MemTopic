# Development Plan: Circular Navigation System

**브랜치**: `feature/41-circular-navigation`

## 목표

PlayTopicView와 TopicListView 간의 순환 네비게이션 구현 및 AccountView에서 TopicListView로의 일관된 네비게이션 개선

## 작업 스펙

- [ ] PlayTopicView에서 백 버튼 시 TopicListView로 이동하도록 수정
- [ ] TopicListView에서 백 버튼 시 PlayTopicView로 이동하도록 수정 (최근 재생 토픽이 있는 경우)
- [ ] 순환 네비게이션 로직 구현 (PlayTopicView ↔ TopicListView)
- [ ] AccountView에서 백 버튼 시 TopicListView로 이동 구현
- [ ] 더블 백 투 엑시트는 TopicListView에서만 유지

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

기술적 고려사항:

- BackHandler 조건 수정
- NavController.navigate()로 화면 전환
- playbackRepository를 통한 최근 재생 토픽 확인
- 네비게이션 스택 관리 (popBackStack 등)

## 진행 상황

- 🔄 계획 수립 중
