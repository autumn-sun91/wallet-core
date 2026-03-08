rootProject.name = "wallet-core"
include(
    "core:wallet-domain",
    "core:wallet-application",
)

include(
    // adapter-in port
    "adapter:wallet-external-api",
    "adapter:wallet-kafka",
    // adapter-out port
    "adapter:wallet-persistence",
)

include(
    "bootstrap:point-bootstrap",
    "bootstrap:point-consumer-bootstrap",
)
