# Entity PDC

## EntityPDC

`EntityPDC` allows you to match entities based on values stored in their **PersistentDataContainer (PDC)**.



```yaml
match-entity:
  entity-pdc:
    "myplugin:rank": "10~20"        # Numeric range match
    "myplugin:power": ">=100"       # Numeric comparison
    "myplugin:elite": true          # Boolean match
    "myplugin:type": "boss*"        # Fuzzy match (starts with "boss")
    "myplugin:category": "*elite*"  # Fuzzy match (contains "elite")
```

#### Matching Rules Type

| Type               | Example                          | Description                                                |
| ------------------ | -------------------------------- | ---------------------------------------------------------- |
| Numeric range      | `"10~20"`                        | Matches values between 10 and 20                           |
| Numeric comparison | `">=5"`, `"<10"`                 | Supports `5~10`, `>=5`, `<=10`, `>5`, `<10`, `=8`, or `8`. |
| String wildcard    | `"boss*"`, `"*boss"`, `"*boss*"` | Supports `*` wildcard matching                             |
| String exact       | `"Boss"`                         | Case-insensitive exact match                               |
| Boolean            | `true` / `false`                 | Matches boolean values                                     |

#### Notes&#x20;

* Keys must be valid NamespacedKeys (e.g. myplugin:rank).
* The rule reads data from the entity’s PDC and compares it with the provided values.
* If any key matches successfully, the rule returns true.
