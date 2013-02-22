# composer

Compose schedules

Cypher queries
==============
`
start n = node:equipment(name="WSC1")
MATCH n-[r]-m
return n.name, type(r), m.name, r.at;

start n = node:container(name="UMXU253048")
MATCH n-[r]-m
return n.name, type(r), m.name, r.at;
`

## Usage

If you use cake, substitute 'lein' with 'cake' below. Everything should work fine.

```bash
lein deps
lein run
```

## License

Copyright (C) 2013 Timothy Pratley

Distributed under the Eclipse Public License, the same as Clojure.

