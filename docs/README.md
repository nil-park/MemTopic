# MemTopic Documentation

이 디렉토리는 MemTopic 프로젝트의 모든 문서를 체계적으로 관리합니다.

## 📁 디렉토리 구조

```
docs/
├── README.md                    # 이 파일 - 문서 가이드라인 및 인덱스
├── reports/                     # 분석 및 검토 리포트
│   ├── code-reviews/           # 코드 리뷰 결과
│   │   ├── YYYY-MM-DD-{description}.md
│   │   └── template.md
│   ├── performance/            # 성능 분석 (필요시 생성)
│   │   ├── YYYY-MM-DD-{metric}-analysis.md
│   │   └── benchmarks/
│   ├── security/              # 보안 검토 (필요시 생성)
│   │   ├── YYYY-MM-DD-security-assessment.md
│   │   └── vulnerability-scans/
│   └── ui-ux/                 # UI/UX 평가 (필요시 생성)
│       ├── YYYY-MM-DD-{component}-review.md
│       └── user-testing/
├── architecture/              # 아키텍처 문서 (필요시 생성)
│   ├── overview.md           # 전체 시스템 개요
│   ├── database-schema.md    # DB 설계 문서
│   ├── api-design.md         # API 설계 문서
│   └── migration-plans/      # 마이그레이션 계획들
└── development/              # 개발 가이드 (필요시 생성)
    ├── setup.md             # 개발 환경 설정 가이드
    ├── coding-standards.md   # 코딩 표준 및 컨벤션
    ├── testing-guide.md     # 테스트 작성 및 실행 가이드
    └── deployment.md        # 배포 가이드
```

## 📝 문서 작성 가이드라인

### 파일 명명 규칙

1. **리포트 파일**: `YYYY-MM-DD-{설명}.md`
   - 예: `2025-08-29-architecture-review.md`
   - 예: `2025-09-01-security-audit.md`

2. **일반 문서**: `{주제}.md` (소문자, 하이픈 사용)
   - 예: `database-schema.md`
   - 예: `coding-standards.md`

### 문서 템플릿

각 카테고리별로 템플릿 파일을 제공합니다:

- `reports/code-reviews/template.md`
- 기타 필요시 추가

### 문서 업데이트 규칙

1. **새로운 분석/리뷰**: `reports/` 하위 적절한 카테고리에 날짜별로 작성
2. **아키텍처 변경**: `architecture/` 문서 업데이트 후 변경 내역을 리포트로 기록
3. **개발 가이드 변경**: `development/` 문서 업데이트

## 📚 현재 문서 목록

### Reports

- [2025-08-29 종합 프로젝트 분석](./reports/code-reviews/2025-08-29-comprehensive-analysis.md)

### Architecture

- *작성 예정*

### Development  

- *작성 예정*

## 🔗 외부 참조

- [프로젝트 README](../README.md)
- [라이선스](../LICENSE)

---

**문서 기여 방법:**

1. 필요한 디렉토리가 없으면 생성
2. 적절한 카테고리와 명명 규칙에 따라 파일 생성
3. 템플릿이 있다면 해당 템플릿 사용
4. 이 README.md의 "현재 문서 목록" 섹션에 링크 추가
5. 커밋 메시지에 `docs:` 프리픽스 사용
