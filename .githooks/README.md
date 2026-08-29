# Git Hooks Configuration

This directory contains repo-wide Git hooks to enforce repository best practices.

## Automatic Enforcement

To enable these hooks locally, run:

```bash
git config core.hooksPath .githooks
```

## Configured Hooks

- `pre-push`: Prevents direct pushes to the `main` branch, prompting developers to push to feature branches and create Pull Requests.
