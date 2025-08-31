# Branch Naming Convention

MemTopic 프로젝트의 브랜치 명명 규칙입니다.

## 📋 기본 형식

```
{type}/{issue-number}-{brief-description}
```

## 🏷️ 타입 카테고리

| 타입 | 설명 | 예시 |
|------|------|------|
| `feature/` | 새로운 기능 추가 | `feature/42-add-user-authentication` |
| `fix/` | 버그 수정 | `fix/43-memory-leak-fix` |
| `hotfix/` | 긴급 수정 (프로덕션) | `hotfix/44-critical-security-patch` |
| `refactor/` | 코드 리팩토링 | `refactor/45-cleanup-database-layer` |
| `docs/` | 문서 작업 | `docs/46-update-api-documentation` |
| `chore/` | 의존성, 설정, 빌드 등 | `chore/47-update-dependencies` |
| `test/` | 테스트 관련 작업 | `test/48-add-unit-tests` |
| `perf/` | 성능 개선 | `perf/49-optimize-database-queries` |

## 📐 명명 규칙

### ✅ 준수사항

1. **소문자와 하이픈만 사용**
2. **이슈 번호 필수 포함**
3. **설명은 간결하게** (30자 이내 권장)
4. **영어 사용** (일관성 유지)
5. **동사 형태 사용** (`add`, `fix`, `update` 등)

### ❌ 피해야 할 것

- 대문자 사용: ~~`Feature/42-Add-Auth`~~
- 공백 사용: ~~`feature/42 add auth`~~
- 특수문자: ~~`feature/42_add@auth`~~
- 긴 설명: ~~`feature/42-add-comprehensive-user-authentication-system-with-jwt`~~
- 한글 사용: ~~`feature/42-사용자-인증-추가`~~

## 💡 좋은 브랜치명 예시

```bash
# 기능 추가
feature/15-add-voice-selection
feature/23-implement-dark-mode
feature/31-add-export-functionality

# 버그 수정
fix/18-audio-playback-crash
fix/25-topic-deletion-error
fix/33-memory-leak-fix

# 리팩토링
refactor/20-extract-audio-service
refactor/28-simplify-topic-model

# 문서 작업
docs/22-add-api-documentation
docs/30-update-readme

# 의존성/설정
chore/19-upgrade-electron
chore/27-update-build-config
```

## 🔄 워크플로우 연계

1. **이슈 생성** → GitHub Issues에서 이슈 번호 확인
2. **브랜치 생성** → 규칙에 따라 브랜치명 작성
3. **개발 플랜** → `docs/development/plans/`에 계획서 작성
4. **Pull Request** → 브랜치명과 이슈 번호로 연결

## 🛠️ Git 명령어 예시

```bash
# 브랜치 생성 및 체크아웃
git checkout -b feature/42-add-user-authentication

# 원격 브랜치에 푸시
git push -u origin feature/42-add-user-authentication
```

---

**참고:** 브랜치 생성 전 반드시 해당 이슈가 존재하는지 확인하세요.
