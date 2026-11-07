# Structured Concurrency Examples

This package contains **14 examples** demonstrating Java 25+ **Structured Concurrency** via `java.util.concurrent.StructuredTaskScope`. Each example is a self-contained class showing a different pattern, joiner, or configuration.

All examples use modern Java features such as **value records** and **pattern matching for switch**.

---

## 📚 Basics

### 1. `StructuredConcurrencyBasic`

> The simplest Structured Concurrency example.

Forks two subtasks — fetching a user and fetching their last order — and waits for both to complete using the default joiner. If either subtask fails, `scope.join()` throws `ExecutionException`.

| | |
|---|---|
| **Key concept** | Basic fork-join with automatic thread lifecycle management |
| **Joiner** | Default (`StructuredTaskScope.open()`) |

<details>
<summary>🔽 Show more details</summary>

- Demonstrates the core fork-join pattern
- Two subtasks run concurrently on virtual threads
- `scope.join()` blocks until all subtasks complete
- On failure, `ExecutionException` wraps the cause

</details>

---

### 2. `StructuredConcurrencyRunnable`

> Forking subtasks that return `void` (Runnable-style).

Sends notifications via email, SMS, and push services concurrently using `awaitAllSuccessfulOrThrow()`. Any failure propagates as a `RuntimeException`.

| | |
|---|---|
| **Key concept** | Forking side-effect tasks where no result is needed |
| **Joiner** | `awaitAllSuccessfulOrThrow()` |

<details>
<summary>🔽 Show more details</summary>

- Subtasks return `void` instead of a result
- Useful for fire-and-forget operations like notifications
- All tasks must succeed; any failure throws immediately

</details>

---

### 3. `StructuredConcurrencyAwaitAll`

> Explicit joiner selection for the basic scenario.

Same scenario as the basic example, but explicitly uses `StructuredTaskScope.Joiner.awaitAllSuccessfulOrThrow()`. This joiner waits for all subtasks to complete and returns `null` on success, throwing `ExecutionException` on any failure.

| | |
|---|---|
| **Key concept** | Explicit joiner selection — `awaitAllSuccessfulOrThrow()` vs the default joiner |
| **Joiner** | `awaitAllSuccessfulOrThrow()` |

<details>
<summary>🔽 Show more details</summary>

- Shows the difference between the default joiner and explicit joiner
- `awaitAllSuccessfulOrThrow()` returns `null` on success
- Makes the joiner intent explicit in the code

</details>

---

## 🏁 Joiner Patterns

### 4. `StructuredConcurrencyAllSuccessful`

> Fetching a collection of resources where all results are required.

Fetches multiple HTTP endpoints concurrently using `allSuccessfulOrThrow()`. All endpoints are fetched in parallel, and the results are collected into a list. If any endpoint fails, the entire scope fails.

| | |
|---|---|
| **Key concept** | Fetching a collection of resources where all results are required |
| **Joiner** | `allSuccessfulOrThrow()` |

<details>
<summary>🔽 Show more details</summary>

- Returns a `List` of all results on success
- All subtasks must complete successfully
- Ideal for batch data fetching scenarios

</details>

---

### 5. `StructuredConcurrencyAllUntil`

> Early termination — stop waiting when a condition is met.

Invokes a list of callable tasks using `allUntil()` with a custom predicate that stops when the first subtask fails. After joining, only the successful subtask results are collected and returned.

| | |
|---|---|
| **Key concept** | Early termination — stop waiting when a condition is met, then process only the successful results |
| **Joiner** | `allUntil(predicate)` |

<details>
<summary>🔽 Show more details</summary>

- Predicate controls when to stop waiting
- Can process partial results before a failure
- Useful for scenarios where some failures are acceptable

</details>

---

### 6. `StructuredConcurrencyAnySuccessful`

> Race condition pattern — take the first successful result and cancel the rest.

Verifies an address by querying three providers concurrently using `anySuccessfulOrThrow()`. The first provider to succeed wins; the remaining subtasks are automatically canceled.

| | |
|---|---|
| **Key concept** | Race condition pattern — take the first successful result and cancel the rest |
| **Joiner** | `anySuccessfulOrThrow()` |

<details>
<summary>🔽 Show more details</summary>

- First success wins; other subtasks are canceled
- Useful for fallback or redundancy scenarios
- Reduces latency by taking the fastest response

</details>

---

## 🛡️ Error Handling

### 7. `StructuredConcurrencyExceptionHandling`

> Translating structured concurrency exceptions into meaningful application-level errors.

Catches `ExecutionException` from `scope.join()` and uses pattern matching for switch to inspect the cause — distinguishing between `TimeoutException`, `IOException`, and custom `UserNotFoundException`.

| | |
|---|---|
| **Key concept** | Translating structured concurrency exceptions into meaningful application-level errors |
| **Java feature** | Pattern matching for switch |

<details>
<summary>🔽 Show more details</summary>

- Uses `switch` with pattern matching to inspect exception causes
- Maps low-level exceptions to domain-specific errors
- Demonstrates proper exception translation strategy

</details>

---

### 8. `StructuredConcurrencyCustomException`

> Customizing the exception type thrown by the joiner.

Uses `allSuccessfulOrThrow(CompletionException::new)` so that on failure, a `CompletionException` is thrown instead of the default `ExecutionException`.

| | |
|---|---|
| **Key concept** | Customizing the exception type thrown by the joiner |
| **Joiner** | `allSuccessfulOrThrow(exceptionSupplier)` |

<details>
<summary>🔽 Show more details</summary>

- Default joiner throws `ExecutionException` on failure
- Custom supplier allows any exception type
- Useful for matching existing exception handling patterns

</details>

---

## 🔧 Advanced Patterns

### 9. `StructuredConcurrencyDynamicForking`

> Dynamic forking with per-scope timeout configuration.

Dynamically forks subtasks based on a list of product IDs, each querying multiple suppliers. Uses a `ThreadFactory` customizer to apply a timeout via `cf.withTimeout(timeout)`.

| | |
|---|---|
| **Key concept** | Dynamic forking with per-scope timeout configuration |
| **Configuration** | `ThreadFactory` customizer with timeout |

<details>
<summary>🔽 Show more details</summary>

- Number of subtasks determined at runtime
- Each product queries multiple suppliers concurrently
- Thread factory customizer applies scope-wide settings

</details>

---

### 10. `StructuredConcurrencyNestedScopes`

> Hierarchical concurrency — scopes within scopes for composite operations.

A top-level scope fetches dashboard data, where one subtask itself opens an inner scope to fetch profile and preferences concurrently. The inner scope completes before the outer scope joins.

| | |
|---|---|
| **Key concept** | Hierarchical concurrency — scopes within scopes for composite operations |
| **Pattern** | Nested `StructuredTaskScope` |

<details>
<summary>🔽 Show more details</summary>

- Inner scope runs as a subtask of the outer scope
- Demonstrates hierarchical task decomposition
- Inner scope must complete before outer scope joins

</details>

---

### 11. `StructuredConcurrencySubtaskState`

> Inspecting individual subtask states after completion to build a detailed report.

Reports the health of multiple endpoints by inspecting each subtask's state after joining. Uses `allUntil(_ -> false)` to wait for all subtasks without early cancellation, then iterates over each subtask to check whether it succeeded, failed, or was canceled.

| | |
|---|---|
| **Key concept** | Inspecting individual subtask states after completion to build a detailed report |
| **Joiner** | `allUntil(_ -> false)` |

<details>
<summary>🔽 Show more details</summary>

- Each subtask has a state: `SUCCESS`, `FAILURE`, or `CANCELLED`
- Enables building detailed health reports
- Useful for monitoring and diagnostics

</details>

---

## ⚙️ Configuration

### 12. `StructuredConcurrencyThreadNaming`

> Custom thread naming for better debugging and log traceability.

Uses `Thread.ofVirtual().name("profile-", 0).factory()` to produce threads named `profile-0`, `profile-1`, etc., which aids debugging and observability.

| | |
|---|---|
| **Key concept** | Custom thread naming for better debugging and log traceability |
| **API** | `Thread.ofVirtual().name(prefix, start)` |

<details>
<summary>🔽 Show more details</summary>

- Named threads appear in logs and stack traces
- Makes it easy to identify which scope a thread belongs to
- Improves debugging experience significantly

</details>

---

### 13. `StructuredConcurrencyTimeout`

> Enforcing a time limit on concurrent operations.

Applies a global timeout to all subtasks in the scope using `cf.withTimeout(timeout)`. If any subtask exceeds the timeout, the scope fails with an `ExecutionException` wrapping a `TimeoutException`.

| | |
|---|---|
| **Key concept** | Enforcing a time limit on concurrent operations |
| **Configuration** | `ThreadFactory` customizer with `withTimeout()` |

<details>
<summary>🔽 Show more details</summary>

- Timeout applies to all subtasks in the scope
- Prevents slow subtasks from blocking indefinitely
- Timeout exception is wrapped in `ExecutionException`

</details>

---

## 🔄 Migration

### 14. `StructuredConcurrencyMigration`

> Before-and-after migration pattern from traditional concurrency to structured concurrency.

Shows the same fetch-profile scenario using the clean `StructuredTaskScope` API instead of manual thread management with `CompletableFuture` or `ExecutorService`.

| | |
|---|---|
| **Key concept** | Before-and-after migration pattern from traditional concurrency to structured concurrency |
| **Comparison** | `CompletableFuture` vs `StructuredTaskScope` |

<details>
<summary>🔽 Show more details</summary>

- Shows legacy `CompletableFuture` approach for comparison
- Demonstrates cleaner structured concurrency alternative
- Highlights reduced boilerplate and better structure

</details>

---

## Quick Reference — Joiners

| Joiner | Behavior | Returns on Success | Throws on Failure |
|---|---|---|---|
| **Default** (`StructuredTaskScope.open()`) | Waits for all; throws on first failure | — | `ExecutionException` |
| `awaitAllSuccessfulOrThrow()` | Waits for all; void result | `null` | `ExecutionException` |
| `allSuccessfulOrThrow()` | Waits for all; collects results | `List<T>` | `ExecutionException` |
| `allUntil(predicate)` | Waits until all complete or predicate is `true` | `List<T>` | `ExecutionException` |
| `anySuccessfulOrThrow()` | Returns first success; cancels rest | `T` | `ExecutionException` |

---

## Examples at a Glance

| # | Example | Category | Joiner |
|---|---|---|---|
| 1 | `StructuredConcurrencyBasic` | 📚 Basics | Default |
| 2 | `StructuredConcurrencyRunnable` | 📚 Basics | `awaitAllSuccessfulOrThrow()` |
| 3 | `StructuredConcurrencyAwaitAll` | 📚 Basics | `awaitAllSuccessfulOrThrow()` |
| 4 | `StructuredConcurrencyAllSuccessful` | 🏁 Joiners | `allSuccessfulOrThrow()` |
| 5 | `StructuredConcurrencyAllUntil` | 🏁 Joiners | `allUntil(predicate)` |
| 6 | `StructuredConcurrencyAnySuccessful` | 🏁 Joiners | `anySuccessfulOrThrow()` |
| 7 | `StructuredConcurrencyExceptionHandling` | 🛡️ Error Handling | Default |
| 8 | `StructuredConcurrencyCustomException` | 🛡️ Error Handling | `allSuccessfulOrThrow()` |
| 9 | `StructuredConcurrencyDynamicForking` | 🔧 Advanced | Default |
| 10 | `StructuredConcurrencyNestedScopes` | 🔧 Advanced | Default |
| 11 | `StructuredConcurrencySubtaskState` | 🔧 Advanced | `allUntil(_ -> false)` |
| 12 | `StructuredConcurrencyThreadNaming` | ⚙️ Configuration | Default |
| 13 | `StructuredConcurrencyTimeout` | ⚙️ Configuration | Default |
| 14 | `StructuredConcurrencyMigration` | 🔄 Migration | Default |
