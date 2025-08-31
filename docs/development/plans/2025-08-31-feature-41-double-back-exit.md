# Development Plan: Double-Back to Exit App

**브랜치**: `feature/41-double-back-exit`

## 목표
네비게이션 최상위에서 백 버튼을 한 번 눌렀을 때 바로 앱이 종료되는 것을 개선하여, 더블 백(연속 2회 백 버튼)을 눌렀을 때만 앱이 종료되도록 UX 개선

## 작업 스펙
- [ ] 현재 백 버튼 처리 로직 분석
- [ ] 더블 백 감지 시스템 구현
- [ ] 첫 번째 백 버튼 시 토스트 메시지 표시 ("한 번 더 누르면 종료됩니다")
- [ ] 두 번째 백 버튼 (시간 제한 내) 시 앱 종료
- [ ] 시간 제한 후 상태 리셋 로직
- [ ] 최상위 네비게이션에서만 동작하도록 제한

## Claude 지시사항
```
Android Compose Navigation에서 back button 처리:
1. MainActivity에서 BackHandler 구현
2. 최상위 destination인지 확인 ("TopicList"가 시작점)
3. 타이머를 사용한 더블클릭 감지 (일반적으로 2초 제한)
4. Toast 메시지로 사용자 피드백
5. onBackPressedDispatcher.onBackPressed() 호출로 실제 종료

기술적 고려사항:
- Compose의 BackHandler 사용
- NavController.currentDestination으로 현재 위치 확인
- remember + LaunchedEffect로 타이머 관리
- Context를 통한 Toast 표시
```

## 진행 상황
- 🔄 계획 수립 완료