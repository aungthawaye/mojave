#!/usr/bin/env bash
set -euo pipefail

# Root directory to start from (default: current dir)
ROOT_DIR="${1:-.}"

echo "=== Renaming directories named 'io' -> 'org' under: $ROOT_DIR ==="
find "$ROOT_DIR" -depth -type d -name 'io' | while IFS= read -r dir; do
  parent_dir="$(dirname "$dir")"
  new_dir="$parent_dir/org"

  if [ -e "$new_dir" ]; then
    echo "SKIP (target exists): $dir -> $new_dir"
  else
    echo "mv \"$dir\" \"$new_dir\""
    mv "$dir" "$new_dir"
  fi
done

echo
echo "=== Renaming directories named 'mojaloop' -> 'mojave' under: $ROOT_DIR ==="
find "$ROOT_DIR" -depth -type d -name 'mojaloop' | while IFS= read -r dir; do
  parent_dir="$(dirname "$dir")"
  new_dir="$parent_dir/mojave"

  if [ -e "$new_dir" ]; then
    echo "SKIP (target exists): $dir -> $new_dir"
  else
    echo "mv \"$dir\" \"$new_dir\""
    mv "$dir" "$new_dir"
  fi
done

echo
echo "All done."
