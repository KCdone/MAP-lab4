lab4 - social network (MAP 2026-2027)
laborator de refactorizare: aceeasi aplicatie ca la lab3, dar in stil Java 8
(interfete functionale, lambda, referinte la metode, stream-uri, Optional)

how to run
- File -> Open -> folderul Lab4 (IntelliJ il recunoaste ca proiect Gradle)
- run Main (working dir = Lab4, ca sa gaseasca data/users.txt si data/friendships.txt)
- tipul de persistenta se alege din src/main/resources/config.properties (repository.type)
- aceleasi rezultate ca la lab3: 5 comunitati, cea mai sociabila {1,3,4,5,6,7}, longest path = 5

structura (neschimbata fata de lab3)
- domain / domain.validators / exceptions / repository (memory, file, db) / service / ui
- UserCriteria e nou (criterii de cautare ca Predicate<User> statice)

1. interfete functionale si lambda
- Validator<T> are metoda default andThen(Validator<T>). in Main.java se adauga o regula noua
  ca lambda ("username nu incepe cu cifra"), compusa cu andThen peste UserValidator existent.
- Repository are metoda default findAll(Predicate<E> filter). criteriile sunt in UserCriteria
  (byLastName, byFirstNameStartingWith, withoutFriends), combinate cu .and(...) in ConsoleUI.
- RepositoryFactory nu mai are switch, foloseste Map<RepositoryType, Function<Validator<E>, Repository<ID,E>>>.

2. Template Method vs Function (alegere ceruta in enunt)
- am ales composition: o singura clasa FileRepository<ID, E>, cu Function<String,E> si
  Function<E,String> primite in constructor, in loc de AbstractFileRepository + subclase.
- motiv: un repo nou pe fisier = doua lambda-uri in RepositoryFactory, nu o clasa noua.

3. referinte la metode (toate cele 4 tipuri)
- statica: Long::parseLong
- obiect anume: System.out::println (ConsoleUI)
- obiect oarecare: User::getId, Friendship::getId, Community::longestPath
- constructor: InMemoryRepository::new, ArrayList::new

4. stream API - cateva bucle inlocuite
clasa / metoda                 | inainte (lab3)                    | dupa (lab4)
--------------------------------|------------------------------------|-----------------------------
NetworkService.removeUser       | 2 for-uri (caut + sterg prietenii) | filter + map + collect + forEach
NetworkService.findByUsername   | for + if equalsIgnoreCase          | filter + findFirst (Optional)
NetworkService.nextUserId       | for + Math.max manual              | map(getId) + max + orElse
NetworkService.mostSociable     | for + if length > best             | map + max(comparingInt(...))
FileRepository.loadData/write   | BufferedReader/Writer + for         | Files.lines / Files.write + stream

- rezultatele se adauga cu collect (toList/toSet), nu prin adaugare manuala intr-o lista.
- sortarea foloseste Comparator.comparingInt(...), fara compare scris de mana.

5. bucle pastrate (justificate, conform restrictiilor din enunt)
- findComponents: while(!queue.isEmpty()) ramane - e BFS cu stare mutabila, nu simpla parcurgere.
- dfsLongest: for ramane - e backtracking (add, recursie, remove la intoarcere), exceptia
  explicita din enunt.

6. Optional
- Repository.findOne ramane cu null (nu s-a schimbat in Optional<E>, ca sa nu modific toate
  implementarile pentru un beneficiu mic).
- service-ul foloseste Optional peste tot unde cautarea poate sa nu gaseasca nimic
  (findUser, findByUsername, mostSociableCommunity). niciun get() fara verificare.

7. comportament neschimbat
- fisier lipsa, linii corupte (cu numarul liniei), exceptii - toate la fel ca la lab3.
- IOException din Files.lines/Files.write e impachetata tot in RepositoryException.

test data (neschimbate fata de lab3)
- users.txt: 10 useri, friendships.txt -> comunitatile {1,3,4,5,6,7}, {8}, {9}, {10}, {11}
- data/test_corupt/: fisiere cu linii corupte, pentru demo
