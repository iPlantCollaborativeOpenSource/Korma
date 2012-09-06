# Korma

Tasty SQL for Clojure.

## Getting started

Simply add Korma as a dependency to your lein/cake project:

```clojure
[korma "0.3.0-beta9"]
```

For docs and real usage, check out http://sqlkorma.com

To get rid of the ridiculously verbose logging, add the following into src/log4j.xml:

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE log4j:configuration SYSTEM "log4j.dtd">
<log4j:configuration xmlns:log4j="http://jakarta.apache.org/log4j/">
  <logger name="com.mchange">
    <level value="WARN"/>
  </logger>
</log4j:configuration>
```

And include log4j in your project.clj:

```clojure
[log4j "1.2.15" :exclusions [javax.mail/mail
                            javax.jms/jms
                            com.sun.jdmk/jmxtools
                            com.sun.jmx/jmxri]]
```

## Examples of generated queries:

```clojure

(use 'korma.db)
(defdb db (postgres {:db "mydb"
                     :user "user"
                     :password "dbpass"}))

(use 'korma.core)
(defentity users)

(select users)
;; executes: SELECT * FROM users

(select users
  (fields :usersname :id))
;; executes: SELECT users.usersname, users.id FROM users

(select users
  (where {:usersname "chris"}))
;; executes: SELECT * FROM users WHERE (users.usersname = 'chris)'

(select users 
  (where {:active true})
  (order :created)
  (limit 5)
  (offset 3))
;; executes: SELECT * FROM users WHERE (users.active = TRUE) ORDER BY users.created DESC LIMIT 5 OFFSET 3

(select users
  (where (or (= :usersname "chris")
             (= :email "chris@chris.com"))))
;; executes: SELECT * FROM users WHERE (users.usersname = 'chris' OR users.email = 'chris@chris.com')

(select users
  (where {:usersname [like "chris"]
          :status "active"
          :location [not= nil]))
;; executes SELECT * FROM users WHERE (users.usersname LIKE 'chris' AND users.status = 'active' AND users.location IS NOT NULL)

(select users
  (where (or {:usersname "chris"
              :first "chris"}
             {:email [like "%@chris.com"]})))
;; executes: SELECT * FROM users WHERE ((users.usersname = 'chris' AND users.first = 'chris') OR users.email LIKE '%@chris.com)'


(defentity address
 (table-fields :street :city :zip))

(defentity users
 (has-one address))

(select users
 (with address))
;; SELECT address.street, address.city, address.zip FROM users LEFT JOIN address ON users.id = address.users_id

```

### Many-to-Many Relationships

Many-to-many relationships are typically implemented using a join table that
contains foreign keys that reference both tables, and these relationships are
expected to be implemented in this way in Korma.  In the following example,
two entities, `foo` and `bar`, are defined with a many-to-many relationship
between them using the join table `foo_bar`.

```clojure
;; Entities with many-to-many relationships.
(declare foo bar)

(defentity foo
  (entity-fields :baz)
  (many-to-many bar :foo_bar
                {:lfk :foo_id
                 :rfk :bar_id}))

(defentity bar
  (entity-fields :quux)
  (many-to-many foo :foo_bar
                {:lfk :bar_id
                 :rfk :baz_id}))


;; Retrieving entities in many-to-many relationships.
(select foo
  (with bar))
```

The first argument to the macro, `many-to-many`, macro is the name of the
foreign entity.  The second argument is the name of the join table as a
keyword.  The third argument is a map containing the names of the foreign keys
in the join table.  The keyword, `:lfk`, refers to the "left-hand foreign
key."  That is, the column in the join table that refers to the primary key of
the current entity.  The keyword, `:rfk`, refers to the "right-hand foreign
key."  That is, the column in the join table that refers to the primary key of
the foreign entity.

### Retrieving Entities in Has-One and Belongs-To Relationships Separately

By default, Korma, returns the columns of foreign entities in has-one and
belongs-to relationships as keys within the map belonging to the local
entity.  Sometimes, it's convenient to be able to retrieve the entity as a
separate map.  The macro, `with-object`, provides a simple way to do this.

```clojure
;; Entities in a one-to-many relationship.
(declare foo bar)

(defentity foo
  (entity-fields :baz)
  (has-one bar))

(defentity bar
  (entity-fields :quux)
  (haz-one foo))

;; Retrieve an entity in a has-one relationship separately.
(select foo
  (with-object bar))

;; Retrieve an entity in a belongs-to relationship separately.
(select bar
  (with-object foo))
```

The entities are defined just like thay would be in any one-to-many
relationship, but `with-object` is used instead of `with` in the query.

## License

Copyright (C) 2011 Chris Granger

Distributed under the Eclipse Public License, the same as Clojure.
