# Assistant X UI Development Rule

From this point forward, EVERY new Assistant X feature must include its required UI/UX implementation.
The UI and functionality should always remain synchronized.

## Pre-Implementation Checklist
Before implementing a new feature, determine:
1. What backend functionality is required?
2. What UI is required?
3. What states does the UI need?
4. What happens on success?
5. What happens on failure?
6. What settings/permissions are required?

## Design Rules
Preserve the existing Assistant X visual identity:
- dark interface
- glassmorphism
- translucent surfaces
- Assistant X orb
- existing navigation style
- existing typography and spacing
- existing visual hierarchy
Do not redesign the application unnecessarily.

## Feature UI Guidelines
- **Text Commands:** Functional text input, display user commands, Assistant responses, and command execution state.
- **Voice:** Functional microphone, listening/processing/speaking states with visual feedback.
- **Wake Word:** Show appropriate status without constantly showing a fake "listening" state.
- **Task Execution:** Show what is currently happening, progress for multi-step tasks, and completion/failure states.
- **History:** Implement local history view when interactions become available.
- **Menu/Settings:** Gradually populate menu and create proper settings areas (Voice, Wake word, AI, Privacy, etc.) as features are added.
- **Permissions:** Show status and provide controls/instructions.
- **Local AI:** Show model info and clearly indicate if operating locally.
- **Memory:** Provide local memory controls when implemented.
- **Errors:** Clear user-facing state for failures. Do not expose technical stack traces to normal users.

**IMPORTANT:** Never implement a feature as a hidden backend capability with no appropriate user-facing interface unless it is intentionally background-only. Assistant X should feel like ONE complete product.

## Agent Workflow Rules
- **Automatic Commits:** Always commit after every change. Do not ask for permission to commit; just commit with every change using `git commit -am "..."`.
