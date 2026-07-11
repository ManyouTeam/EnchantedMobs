# Place Block

## Place Block

Places a temporary block at action location, then restores the original block after time expires.

Temporary blocks do not drop items. On Spigot, some non-standard block-break methods may not be detected. Paper does not have this issue.

Avoid placing high-value blocks. If the server crashes and temporary-block state is not saved, those blocks may be treated as real blocks after restart and can be dropped.

```yaml
type: place_block
block: COBWEB
duration: 60
```

#### Field Details

**block**

Block material to place.

```yaml
block: POWDER_SNOW
```

**duration**

Duration in ticks.

```yaml
duration: 80
```
