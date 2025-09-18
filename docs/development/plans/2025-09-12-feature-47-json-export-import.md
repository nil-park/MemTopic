# Development Plan - JSON Export/Import Feature

**브랜치**: `feature/47-json-export-import`

## 참고가 필요한 파일 목록

- `android/app/src/main/java/com/nolbee/memtopic/database/TopicDatabase.kt` - 데이터베이스 스키마 및 TypeConverters
- `android/app/src/main/java/com/nolbee/memtopic/database/TopicDao.kt` - Topic 엔티티 구조 및 데이터 접근 방법
- `android/app/src/main/java/com/nolbee/memtopic/database/TopicRepository.kt` - Repository 패턴 구현 방식
- `android/app/src/main/java/com/nolbee/memtopic/database/TopicViewModel.kt` - ViewModel 구조 및 UI 상태 관리
- `android/app/src/main/java/com/nolbee/memtopic/topic_list_view/TopicListView.kt` - 토픽 목록 UI 참조
- `android/app/src/main/java/com/nolbee/memtopic/MenuDrawer.kt` - 메뉴 드로어에 export/import 메뉴 추가 위치
- `android/app/src/main/AndroidManifest.xml` - 파일 시스템 접근 권한 확인
- `android/app/build.gradle.kts` - 필요한 의존성 확인 (JSON 라이브러리 등)

## 변경이 예상되는 파일 목록

- `android/app/src/main/java/com/nolbee/memtopic/MenuDrawer.kt` - Export/Import 메뉴 항목 추가
- `android/app/src/main/java/com/nolbee/memtopic/database/TopicRepository.kt` - Export/Import 로직 추가
- `android/app/src/main/java/com/nolbee/memtopic/database/TopicViewModel.kt` - Export/Import 상태 관리 추가
- `android/app/src/main/AndroidManifest.xml` - 필요시 파일 접근 권한 추가
- `android/app/build.gradle.kts` - kotlinx.serialization 의존성 추가
- 신규 파일: `android/app/src/main/java/com/nolbee/memtopic/utils/TopicExporter.kt` - Topic Export 유틸리티
- 신규 파일: `android/app/src/main/java/com/nolbee/memtopic/utils/TopicImporter.kt` - Topic Import 유틸리티
- 신규 파일: `android/app/src/main/java/com/nolbee/memtopic/utils/ExportImportView.kt` - Export/Import UI 컴포넌트

## 참고가 필요한 레퍼런스 문서

- [Android Storage Access Framework 가이드](https://developer.android.com/guide/topics/providers/document-provider)
- [Kotlinx Serialization 공식 문서](https://github.com/Kotlin/kotlinx.serialization/blob/master/docs/basic-serialization.md)
- [Jetpack Compose File Picker 구현](https://developer.android.com/training/data-storage/shared/documents-files)
- [Android 13+ 파일 권한 변경사항](https://developer.android.com/about/versions/13/behavior-changes-13#granular-media-permissions)
- [Room TypeConverters 문서](https://developer.android.com/training/data-storage/room/referencing-data)
- [Kotlin Coroutines Flow 문서](https://kotlinlang.org/docs/flow.html)

## 목표

MemTopic 앱의 Topic 데이터를 JSON 형식으로 내보내고 가져올 수 있는 기능을 구현하여 사용자가 작성한 토픽들을 백업하고 다른 기기로 이전하거나 친구와 공유할 수 있도록 한다. 간단하고 직관적인 UI를 제공하여 쉽게 토픽을 관리할 수 있게 한다.

## 작업 스펙

- [x] **Step 1: Topic 데이터 직렬화 구조 설계**
  - JSON 스키마 정의 (버전 정보, export 날짜, topics 배열)
  - kotlinx.serialization 의존성 추가
  - Topic 데이터 클래스용 Serializable 래퍼 클래스 생성
  - Date 타입에 대한 커스텀 직렬화 처리

- [x] **Step 2: Topic Export 기능 구현**
  - TopicExporter 유틸리티 클래스 생성
  - TopicRepository에 모든 Topic을 동기적으로 조회하는 메서드 추가
  - Topic 리스트를 JSON으로 변환하는 로직 구현
  - Storage Access Framework를 사용한 파일 저장 구현

- [x] **Step 3: Topic Import 기능 구현**  
  - TopicImporter 유틸리티 클래스 생성
  - JSON 파일 파싱 및 유효성 검증 로직 구현
  - 중복 제목 처리 전략 구현 (제목에 번호 추가 방식)
  - 일괄 insert 트랜잭션 처리

- [x] **Step 4: Export/Import UI 구현**
  - ExportImportView Composable 생성
  - Export 버튼 및 성공/실패 Toast 메시지
  - Import 파일 선택기 및 진행 상태 표시
  - 메뉴 드로어에 "토픽 내보내기/가져오기" 메뉴 추가

- [x] **Step 5: ViewModel 통합**
  - TopicViewModel에 export/import 메서드 추가
  - UI 상태 관리 (loading, success, error)
  - 비동기 처리를 위한 viewModelScope 활용
  - ExportImportState sealed class 추가
  - Hilt 의존성 주입으로 TopicExporter/TopicImporter 제공

- [x] **Step 6: 파일 접근 권한 처리**
  - Android 13 이상 대응 문서 접근 권한 처리 (Storage Access Framework 사용)
  - 구체적인 에러 처리 (SecurityException, FileNotFoundException)
  - 사용자 친화적인 권한 안내 다이얼로그 추가
  - 파일 선택기 취소 시 적절한 처리

- [x] **Step 7: 테스트 및 완성도 향상**
  - 빈 토픽 목록 export 처리 (이미 구현됨 - UI에서 비활성화)
  - 잘못된 JSON 파일 import 시 에러 처리 강화
  - 대량 토픽 (100개 이상) import 성능 최적화 (bulk upsert)
  - 한글 및 특수문자 포함 토픽 제목 처리 확인 (UTF-8 명시)
  - 단위 테스트 작성 (SimpleJsonTest, TestDataGenerator)

## 고려 사항

- **데이터 간소화**: Topic 엔티티만 export/import하여 구현 복잡도 최소화
- **ID 처리**: Import 시 기존 ID는 무시하고 새로운 ID 자동 생성 (autoGenerate)
- **중복 처리**: 동일한 제목의 토픽이 있을 경우 "(1)", "(2)" 등 번호 추가
- **파일 형식**: "memtopic_topics_YYYYMMDD_HHmmss.json" 명명 규칙 사용
- **사용자 경험**: Import 전 현재 토픽 개수와 import될 토픽 개수 표시
- **파일 크기**: Topic content가 긴 경우를 대비한 스트리밍 처리 고려
- **언어 설정**: Topic의 options 필드(TTS 설정) 유지

## Claude 지시사항

1. kotlinx.serialization 라이브러리를 사용하여 Topic 데이터를 JSON으로 직렬화
2. Storage Access Framework를 통해 사용자가 직접 파일 저장/불러오기 위치 선택
3. 모든 데이터베이스 작업은 suspend 함수와 Coroutine을 사용하여 비동기 처리
4. Jetpack Compose를 사용하여 기존 UI와 일관된 디자인 유지
5. 사용자 메시지는 간단명료한 한국어로 표시
6. Import 성공 시 "N개의 토픽을 가져왔습니다" 형식의 피드백 제공
7. Export 파일에 메타데이터 포함 (앱 버전, export 날짜, 토픽 개수)

## 수정 또는 확인이 필요한 사항

(구현 진행 중 발견되는 이슈 기록 예정: AI Agent를 별도 지시 없이 수정하지 말 것)

### commit 6cbc6449

- 의도와 다르게 작성된 부분, 또는 버그 설명

  - Desired: 토픽 내보내기/가져오기 화면에서 Back 버튼을 누르면 TopicListView로 이동
  - Got: 내비게이션 최상단이라서 앱 종료
