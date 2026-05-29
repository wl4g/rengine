# Project Conventions

- **Build: `./tools/build/run.sh build-maven`** from repo root. See `tools/build/` for options.
- **Git commit messages must be concise.** Subject under 72 chars. **No `Co-Authored-By` trailers.**
- **Any code, config, or documentation change must follow high cohesion, low coupling.**
  Structure must be clear and concise. Related dependent modules MUST be updated synchronously.
- **E2E secrets**: `source ~/.w4lgshrc.sec` to load SonarQube/Deeplake/Deepseek tokens.

---

# Documentation Index

| Doc | Summary |
|-----|---------|
| [docs/en/devel.md](docs/en/devel.md) | Development guide |
| [docs/en/architecture.md](docs/en/architecture.md) | System architecture |
| [docs/en/deploy-production.md](docs/en/deploy-production.md) | Production deployment |
| [docs/en/configuration-*.md](docs/en/) | Per-module configuration references |

---

# Key Directories

```
common/           Shared POJOs, utils, annotations
apiserver/        REST API server (Spring Boot)
controller/       Rule scheduling controller (Spring Boot)
executor/         Rule execution engine (Quarkus, GraalVM native)
service/          Core rule service layer
job/              Scheduled job module
eventbus/         Event bus adapters (Kafka, Pulsar, RabbitMQ)
bom/              Maven BOM
client/           Multi-language SDKs (Java, Go, Python, Rust, C#)
example/          Example projects (Spring Boot client)
docs/             Documentation (en/zh)
tools/            Build, deploy, DB migration, checkstyle, benchmark scripts
rengine-operator/ Kubernetes operator
rengine-ui/       Web UI frontend
.github/workflows/ CI/CD (build, SonarQube PR/main scan, release)
```
