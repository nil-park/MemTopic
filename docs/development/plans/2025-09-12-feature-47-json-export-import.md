# Development Plan - JSON Export/Import Feature

**브랜치**: `feature/47-json-export-import`

## 참고가 필요한 파일 목록

- `android/app/src/main/java/com/nolbee/memtopic/database/TopicDatabase.kt` - 데이터베이스 스키마 및 엔티티 구조 파악
- `android/app/src/main/java/com/nolbee/memtopic/database/TopicDao.kt` - Topic 엔티티 구조 및 데이터 접근 방법
- `android/app/src/main/java/com/nolbee/memtopic/database/PlaybackDao.kt` - Playback 엔티티 구조
- `android/app/src/main/java/com/nolbee/memtopic/database/AudioCacheDao.kt` - AudioCache 엔티티 구조  
- `android/app/src/main/java/com/nolbee/memtopic/database/SettingsDao.kt` - Setting 엔티티 구조
- `android/app/src/main/java/com/nolbee/memtopic/database/TopicRepository.kt` - Repository 패턴 구현 방식
- `android/app/src/main/java/com/nolbee/memtopic/database/TopicViewModel.kt` - ViewModel 구조 및 UI 상태 관리
- `android/app/src/main/java/com/nolbee/memtopic/MenuDrawer.kt` - 메뉴 드로어에 export/import 메뉴 추가 위치
- `android/app/src/main/AndroidManifest.xml` - 파일 시스템 접근 권한 확인
- `android/app/build.gradle.kts` - 필요한 의존성 확인 (JSON 라이브러리 등)

## 변경이 예상되는 파일 목록

- `android/app/src/main/java/com/nolbee/memtopic/MenuDrawer.kt` - Export/Import 메뉴 항목 추가
- `android/app/src/main/java/com/nolbee/memtopic/database/TopicRepository.kt` - Export/Import 로직 추가
- `android/app/src/main/java/com/nolbee/memtopic/database/TopicViewModel.kt` - Export/Import 상태 관리 추가
- `android/app/src/main/AndroidManifest.xml` - 필요시 파일 접근 권한 추가
- `android/app/build.gradle.kts` - kotlinx.serialization 의존성 추가
- 신규 파일: `android/app/src/main/java/com/nolbee/memtopic/utils/DataExporter.kt` - Export 유틸리티
- 신규 파일: `android/app/src/main/java/com/nolbee/memtopic/utils/DataImporter.kt` - Import 유틸리티
- 신규 파일: `android/app/src/main/java/com/nolbee/memtopic/utils/ExportImportView.kt` - Export/Import UI 컴포넌트

## 참고가 필요한 레퍼런스 문서

- [Android Storage Access Framework 가이드](https://developer.android.com/guide/topics/providers/document-provider)
- [Kotlinx Serialization 공식 문서](https://github.com/Kotlin/kotlinx.serialization/blob/master/docs/basic-serialization.md)
- [Room Database Export/Import 가이드](https://developer.android.com/training/data-storage/room/prepopulate)
- [Jetpack Compose File Picker 구현](https://developer.android.com/training/data-storage/shared/documents-files)
- [Android 13+ 파일 권한 변경사항](https://developer.android.com/about/versions/13/behavior-changes-13#granular-media-permissions)
- [JSON Schema 사양](https://json-schema.org/specification.html)
- [Room TypeConverters 문서](https://developer.android.com/training/data-storage/room/referencing-data)
- [Kotlin Coroutines Flow 문서](https://kotlinlang.org/docs/flow.html)

## 목표

MemTopic 앱의 모든 데이터(Topic, Playback, AudioCache, Settings)를 JSON 형식으로 내보내고 가져올 수 있는 기능을 구현하여 사용자가 데이터를 백업하고 다른 기기로 이전할 수 있도록 한다. 사용자 친화적인 UI를 제공하여 쉽게 데이터를 관리할 수 있게 한다.

## 작업 스펙

- [ ] **Step 1: 데이터 모델 및 직렬화 구조 설계**
  - JSON 스키마 정의 (버전 정보, 생성 날짜, 각 엔티티별 배열 구조)
  - kotlinx.serialization 의존성 추가 및 데이터 클래스에 @Serializable 어노테이션 적용 방안 검토
  - Date 타입에 대한 커스텀 직렬화 전략 정의

- [ ] **Step 2: Export 기능 구현**
  - DataExporter 유틸리티 클래스 생성
  - Repository에서 모든 데이터를 조회하는 메서드 구현
  - 데이터를 JSON으로 변환하고 파일로 저장하는 로직 구현
  - Storage Access Framework를 사용한 파일 저장 위치 선택 구현

- [ ] **Step 3: Import 기능 구현**  
  - DataImporter 유틸리티 클래스 생성
  - JSON 파일 파싱 및 유효성 검증 로직 구현
  - 기존 데이터와의 충돌 처리 전략 구현 (덮어쓰기, 병합, 건너뛰기 옵션)
  - 트랜잭션 처리로 원자성 보장

- [ ] **Step 4: UI 컴포넌트 구현**
  - ExportImportView Composable 생성
  - Export 진행 상태 표시 UI (프로그레스 바, 성공/실패 메시지)
  - Import 파일 선택 UI 및 충돌 해결 옵션 다이얼로그
  - 메뉴 드로어에 Export/Import 메뉴 항목 추가

- [ ] **Step 5: ViewModel 및 상태 관리**
  - TopicViewModel에 export/import 관련 상태 추가
  - 비동기 처리를 위한 Coroutine 구현
  - 에러 처리 및 사용자 피드백 메커니즘 구현

- [ ] **Step 6: 권한 처리 및 파일 시스템 접근**
  - Android 13 이상 대응 미디어 권한 처리
  - AndroidManifest.xml 권한 설정 업데이트
  - 런타임 권한 요청 로직 구현

- [ ] **Step 7: 테스트 및 예외 처리**
  - 대용량 데이터 export/import 테스트
  - 손상된 JSON 파일 처리 테스트
  - 부분 import 실패 시 롤백 처리 확인
  - AudioCache 크기 제한 고려사항 점검

## 고려 사항

- **데이터 무결성**: Import 시 foreign key 제약 조건 위반 방지
- **성능**: AudioCache 데이터가 클 수 있으므로 청크 단위 처리 고려
- **호환성**: 향후 스키마 변경 시 하위 호환성 유지를 위한 버전 관리
- **사용자 경험**: 장시간 소요되는 작업에 대한 명확한 진행 상태 표시
- **보안**: 민감한 설정 정보(API 키 등)의 export 제외 여부 검토
- **파일 크기**: JSON 파일이 너무 커지지 않도록 AudioCache export 옵션 제공
- **충돌 해결**: 동일한 ID를 가진 Topic이 있을 때 처리 방식 (타임스탬프 기반 최신 버전 유지)

## Claude 지시사항

1. kotlinx.serialization 라이브러리를 사용하여 JSON 직렬화를 구현할 것
2. Storage Access Framework를 통해 사용자가 직접 파일 저장/불러오기 위치를 선택하도록 구현
3. 모든 데이터베이스 작업은 suspend 함수와 Coroutine을 사용하여 비동기 처리
4. Jetpack Compose를 사용하여 일관된 UI 디자인 유지
5. 에러 메시지는 사용자가 이해하기 쉬운 한국어로 표시
6. Export 파일명은 "memtopic_backup_YYYYMMDD_HHmmss.json" 형식 사용
7. Import 전 현재 데이터 백업을 권장하는 경고 메시지 표시

## 수정 또는 확인이 필요한 사항

(구현 진행 중 발견되는 이슈 기록 예정)

### commit {미정}

- 의도와 다르게 작성된 부분, 또는 버그 설명

  - Desired:
  - Got:
