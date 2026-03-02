# Cascading Guidelines Bootstrap

To ensure consistent behavior and adherence to project-specific and organizational standards, you (Junie) must aggregate and apply guidelines from multiple levels of the directory structure.

### Guideline Discovery & Aggregation

Before processing any request, you must check for the existence of `guidelines.md` files in the following locations, relative to the current project directory (let's call it `.`):
1.  `./.junie/guidelines.md` (Current project)
2.  `../.junie/guidelines.md` (One level up)
3.  `../../.junie/guidelines.md` (Two levels up)

**Note:** The search must not exceed two steps towards the filesystem root from the current project directory. If the root is reached on step 2, or if you have insufficient permissions to read the `..` directory in step 2, do not continue to step 3.

### Precedence and Conflict Resolution

When guidelines are aggregated from multiple levels, the following precedence logic applies:

*   Rules in the current project (`./.junie/guidelines.md`) take highest precedence.

*   Rules in the immediate parent (`../.junie/guidelines.md`) take precedence over those in the grandparent (`../../.junie/guidelines.md`).

#### Conflict Handling

*   **Brave Mode**

    If you are in **Brave mode**, you must automatically apply the precedence logic described above to resolve any contradictions between rules.

* **Non-Brave Mode**

  If you detect a contradiction between rules from different levels when *not* in Brave mode, you **must** pause and ask the user:

  > "I have detected conflicting rules in the cascading guidelines. Would you like me to apply the standard precedence logic for all conflicts related to this request, or would you prefer to resolve them individually?"

### Responsibility

While you should attempt to identify obvious contradictions (e.g., "Use tabs" vs. "Use spaces"), the user is responsible for the granularity and clarity of the rules provided at each level. If a rule is ambiguous, seek clarification as per standard operating procedures.
