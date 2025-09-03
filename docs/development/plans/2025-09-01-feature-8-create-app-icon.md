# Development Plan: Create App Icon

**브랜치**: `feature/8-create-app-icon`

## 목표

MemTopic 애플리케이션의 아이콘을 생성하여 브랜드 아이덴티티를 확립하고 사용자 경험을 향상시킨다.

## 시안

[/assets/icons/drafts/AppIcon.png](/assets/icons/drafts/AppIcon.png)

## 작업 스펙

- [x] 아이콘 시안을 벡터 그래픽으로 변환하되 안드로이드 형식에 맞게
- [x] 아이콘 파일들을 assets 폴더에 정리하되, 필요한 부분을 안드로이드 프로젝트 내에 복사
- [x] 테스트: 아이콘이 올바르게 표시되는지 확인
- [x] 디자인 심플화: 복잡한 톱니바퀴 제거하고 깔끔한 전구 모양으로 최종 결정

## Claude 지시사항

- 먼저 현재 프로젝트 구조를 파악하여 아이콘 파일들을 저장할 적절한 위치를 찾아라
  - 현재는 안드로이드 프로젝트 뿐이지만 아이콘의 원형 보존을 위해 시안을 저장
- 안드로이드 아이콘은 다음 크기 가이드를 준수할 것

  ```plaintext
  전체 캔버스: 1024×1024px
  ├── 공식 Safe Zone: 626×626px (중앙)
  └── 실무 권장: 580×580px (중앙)
  ```

- 시안은 살리되, 원형 테두리는 없애고 정사각형 디자인
  - 원, 삼각형 등 특별한 형태 지양

## 진행 상황

- ✅ 브랜치 생성 완료
- ✅ 개발 계획 수립 완료
- ✅ PNG → SVG 벡터화 완료 ([Convertio](https://convertio.co/kr/png-svg/) 사용)
- ✅ Android XML drawable 생성 완료
- ✅ 파일 구조 정리 및 문서화 완료
- ✅ 디자인 심플화 (톱니바퀴 → 깔끔한 전구)
- ✅ 최종 커밋 및 푸시 완료
- ✅ PR #53 생성 완료 (<https://github.com/nil-park/MemTopic/pull/53>)

## 결과물

- **SVG 파일**: `/assets/icons/v1/AppIconVectorized.svg`
- **Android XML**: `/android/app/src/main/res/drawable/ic_launcher_foreground.xml`
- **색상**: 배경 그라데이션 (#D2589C → #390A26 → #80D6F6), 전구 #FFFFFF
- **특징**: 심플한 전구 디자인 (복잡한 톱니바퀴 제거)
- **크기**: 1024×1024px (Android 런처 최적화)

## 최종 디자인 변경사항

1. **색상 조정**: 더 생동감 있는 핑크-블루 그라데이션으로 변경
2. **디자인 심플화**: 복잡한 톱니바퀴 메커니즘 제거
3. **가독성 향상**: 깔끔한 흰색 전구만 남겨 브랜드 인식도 개선
4. **Play 아이콘 실험**: 중간 과정에서 Play 삼각형 시도했으나 최종적으로 제거

## 파일 구조

```plaintext
assets/icons/
├── drafts/
│   ├── AppIcon.png (원본 시안)
│   └── AppIconVectorized.svg (초기 벡터화 버전)
├── docs/
│   └── android-app-icon-creation-guide.md (생성 가이드)
└── v1/
    └── AppIconVectorized.svg (최종 완성 버전)

android/app/src/main/res/drawable/
└── ic_launcher_foreground.xml (Android XML drawable)
```

## 상태

**✅ 완료** - 모든 작업 완료, PR #53 생성됨. 이제 리뷰 후 메인 브랜치 머지 대기 중.
