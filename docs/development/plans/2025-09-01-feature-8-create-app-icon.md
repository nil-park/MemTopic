# Development Plan: Create App Icon

**브랜치**: `feature/8-create-app-icon`

## 목표

MemTopic 애플리케이션의 아이콘을 생성하여 브랜드 아이덴티티를 확립하고 사용자 경험을 향상시킨다.

## 시안

[/assets/icons/drafts/AppIcon.png](/assets/icons/drafts/AppIcon.png)

## 작업 스펙

- [x] 아이콘 시안을 벡터 그래픽으로 변환하되 안드로이드 형식에 맞게
- [ ] 아이콘 파일들을 assets 폴더에 정리하되, 필요한 부분을 안드로이드 프로젝트 내에 복사
- [ ] 테스트: 아이콘이 올바르게 표시되는지 확인

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

## 결과물

- **파일**: `/assets/icons/drafts/AppIconVectorized.svg`
- **색상**: 배경 그라데이션 (#3498DB → #2C3E50), 모든 요소 #F1C40F
- **특징**: 전구 중앙 톱니바퀴 부분 투명 처리
