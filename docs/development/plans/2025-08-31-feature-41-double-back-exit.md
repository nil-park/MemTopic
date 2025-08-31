# Development Plan: Double-Back to Exit App

**브랜치**: `feature/41-double-back-exit`

## 목표

네비게이션 최상위에서 백 버튼을 한 번 눌렀을 때 바로 앱이 종료되는 것을 개선하여, 더블 백(연속 2회 백 버튼)을 눌렀을 때만 앱이 종료되도록 UX 개선

## 작업 스펙

- [x] 현재 백 버튼 처리 로직 분석
- [x] 더블 백 감지 시스템 구현
- [x] ~~첫 번째 백 버튼 시 토스트 메시지 표시~~ (요구사항 변경: 토스트 없이 내부적으로만 동작)
- [x] 두 번째 백 버튼 (시간 제한 내) 시 앱 종료
- [x] 시간 제한 후 상태 리셋 로직 (2초 threshold)
- [x] TopicList에서만 동작하도록 제한

## Claude 지시사항

Android Compose Navigation에서 back button 처리:

1. MainActivity에서 BackHandler 구현 ✅
2. TopicList에서만 double-back 동작하도록 제한 ✅
3. 2초 제한 시간 내 더블클릭 감지 ✅
4. ~~Toast 메시지 표시~~ → 내부적으로만 동작 ✅
5. ComponentActivity.finish() 호출로 앱 종료 ✅

구현된 기술:

- BackHandler with enabled condition
- currentBackStackEntryAsState로 현재 화면 감지
- remember로 백 버튼 누른 시간 상태 관리
- LocalContext로 Activity 접근

## 진행 상황

- ✅ 계획 수립 완료
- ✅ 현재 로직 분석 완료 (기존 BackHandler 없음 확인)
- ✅ 더블 백 감지 시스템 구현 완료
- ✅ TopicList에서만 동작하도록 제한 완료
- ✅ 토스트 없는 내부 동작으로 수정 완료

## 구현 내용

**MainActivity.kt 변경사항:**

- BackHandler 추가 (TopicList에서만 활성화)
- 2초 threshold 내 연속 백 버튼 감지
- 첫 번째: 시간 기록만, 두 번째: 앱 종료
- PlayTopicView → TopicList 기본 네비게이션 유지
