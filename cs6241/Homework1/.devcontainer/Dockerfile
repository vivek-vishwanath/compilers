FROM mcr.microsoft.com/devcontainers/cpp:ubuntu-24.04

ARG LLVM_VERSION=21

USER root

# Install prerequisites once
RUN apt-get update && apt-get install -y \
wget \
lsb-release \
gnupg \
software-properties-common \
&& rm -rf /var/lib/apt/lists/*

# Add LLVM repo and install minimal toolchain

RUN wget -qO- https://apt.llvm.org/llvm.sh | bash -s -- ${LLVM_VERSION} \
&& apt-get install -y \
llvm-${LLVM_VERSION} \
llvm-${LLVM_VERSION}-dev \
clang-${LLVM_VERSION} \
clang-tools-${LLVM_VERSION} \
lld-${LLVM_VERSION} \
&& rm -rf /var/lib/apt/lists/*

# Make LLVM 21 the default (important for VS Code)

RUN update-alternatives --install /usr/bin/clang clang /usr/bin/clang-${LLVM_VERSION} 100 \
&& update-alternatives --install /usr/bin/clang++ clang++ /usr/bin/clang++-${LLVM_VERSION} 100 \
&& update-alternatives --install /usr/bin/llvm-config llvm-config /usr/bin/llvm-config-${LLVM_VERSION} 100 \
&& update-alternatives --install /usr/bin/opt opt /usr/bin/opt-${LLVM_VERSION} 100

RUN mkdir -p /workspaces && chown vscode:vscode /workspaces

USER vscode
