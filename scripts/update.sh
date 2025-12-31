#!/bin/bash
set -e

# Script to initialize and update git submodules

# Color codes (can be extended by sourcing script-helpers later)
GREEN='\033[0;32m'
NC='\033[0m'

echo -e "${GREEN}Updating git submodules...${NC}"
git submodule update --init --recursive

echo -e "${GREEN}Submodules updated successfully.${NC}"
