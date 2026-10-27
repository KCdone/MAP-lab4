lab4 - social network (MAP 2026-2027)
laborator de refactorizare: aceeasi aplicatie ca la lab3, dar scrisa in stil java 8
(interfete functionale, lambda, referinte la metode, stream-uri, optional)

how to run
- File -> Open -> the Lab4 folder (IntelliJ recognizes it as a Gradle project, from build.gradle.kts)
- run Main (working directory = the Lab4 folder, the default for Gradle projects,
  so it can find data/users.txt and data/friendships.txt)
- the persistence type is chosen from src/main/resources/config.properties (repository.type)
- aceleasi rezultate ca la lab3, pe aceleasi fisiere: 5 comunitati, cea mai sociabila
  {1,3,4,5,6,7} cu longest path = 5

structure (layered architecture, neschimbata fata de lab3)
- domain             Entity, Tuple, User, Friendship, Community, UserCriteria (nou, lab4)
- domain.validators  Validator (functional interface, cu andThen), UserValidator, FriendshipValidator
- exceptions         SocialNetworkException (base, unchecked), ValidationException, RepositoryException,
                     EntityNotFoundException, DuplicateEntityException
- repository         Repository (interface, cu findAll(Predicate) si stream() statice), RepositoryType,
                     RepositoryFactory (acum cu Map<RepositoryType, Function<...>>)
  .memory            InMemoryRepository
  .file              FileRepository<ID, E> (inlocuieste AbstractFileRepository + subclasele din lab3)
  .db                UserDbRepository, FriendshipDbRepository (skeletons for lab 4/5)
- service            NetworkService (rescris cu stream-uri)
- ui                 ConsoleUI (forEach + o optiune noua, 9, de demo pentru UserCriteria)
- Main               wires the layers: factory -> repository -> service -> ui, si adauga o regula
                     noua de validare prin andThen

1. interfete functionale si lambda
- Validator<T> e @FunctionalInterface, cu metoda default andThen(Validator<T>) care ruleaza
  ambele validari in ordine. in Main.java se adauga o regula noua ca lambda ("username-ul nu
  incepe cu cifra"), compusa peste new UserValidator() cu andThen, fara sa se modifice
  UserValidator si fara o clasa noua.
- Repository<ID, E> are metoda default List<E> findAll(Predicate<E> filter), care filtreaza
  stream-ul peste findAll() si colecteaza rezultatul. nu s-a adaugat niciun findByXxx in
  repository; criteriile sunt in domain.UserCriteria (byLastName, byFirstNameStartingWith,
  withoutFriends), fiecare o metoda statica ce intoarce Predicate<User>, combinate in ConsoleUI
  cu .and(...) (optiunea 9 din meniu).
- RepositoryFactory nu mai are switch: in constructor se construiesc doua Map<RepositoryType,
  Function<Validator<E>, Repository<ID, E>>> (unul pentru utilizatori, unul pentru prietenii);
  create...Repository(validator) doar cauta tipul in map si aplica functia. un tip nou de
  persistenta = o linie noua in map, fara sa se atinga restul codului (OCP).

2. Template Method vs Function pentru repository-urile pe fisier (alegere ceruta in enunt)
- s-a ales composition in loc de Template Method: AbstractFileRepository si cele doua subclase
  (UserFileRepository, FriendshipFileRepository) au fost sterse si inlocuite cu o singura clasa
  FileRepository<ID, E>, care primeste prin constructor Function<String, E> (linie -> entitate)
  si Function<E, String> (entitate -> linie). conversiile concrete (lineToUser/userToLine,
  lineToFriendship/friendshipToLine) sunt scrise ca lambda-uri/metode private in RepositoryFactory,
  acolo unde oricum se stiu fisierele si cheile din config.properties.
- de ce composition: Template Method cerea o clasa noua pentru fiecare entitate persistata in
  fisier, doar ca sa suprascrie 2 metode; cu Function, acelasi FileRepository se refoloseste
  pentru User si Friendship fara nicio clasa in plus, iar RepositoryFactory ramane singurul loc
  care stie formatul liniilor (coerent cu rolul lui de Simple Factory).
- dezavantaj mic: conversiile nu mai sunt langa entitate (ca in AbstractFileRepository), ci in
  factory; acceptabil cat timp sunt doar 2 entitati persistate in fisier.

3. referinte la metode (toate cele 4 tipuri ceeute in enunt)
- metoda statica: Long::parseLong, folosit in parsarea liniilor, nu era inainte ca metoda ref,
  era deja Long.parseLong(...) deci neschimbat, dar e exemplul cerut de metoda statica.
- metoda de instanta a unui obiect anume: System.out::println, in ConsoleUI.showUsers() si
  showMostSociable() (inainte: for (User u : ...) System.out.println(u)).
- metoda de instanta a unui obiect oarecare: Friendship::getId (in NetworkService.removeUser),
  User::getId (in NetworkService.nextUserId), Community::longestPath
  (Comparator.comparingInt(Community::longestPath)).
- constructor: InMemoryRepository::new (in cele doua Map din RepositoryFactory, ca
  Function<Validator<E>, Repository<ID, E>>) si ArrayList::new (in Repository.findAll(Predicate),
  Collectors.toCollection(ArrayList::new)).

4. stream API - tabel bucle inainte/dupa (minim 5, conform cerintei)
clasa / metoda                          | inainte (lab3)                         | dupa (lab4, operatii de stream)
-----------------------------------------|------------------------------------------|----------------------------------
NetworkService.removeUser                | for peste friendshipRepo.findAll(),      | stream().filter(f -> f.involves(id))
                                          | if involves -> adaugare in lista,        | .map(Friendship::getId)
                                          | apoi al doilea for cu delete             | .collect(toList()).forEach(delete)
NetworkService.findByUsername            | for peste userRepo.findAll(), if         | stream().filter(...equalsIgnoreCase)
(acum private, intoarce Optional<User>)  | equalsIgnoreCase -> return               | .findFirst()
NetworkService.nextUserId                | for peste userRepo.findAll(),            | stream().map(User::getId)
                                          | max = Math.max(max, u.getId())           | .max(Long::compareTo).orElse(0L)+1
NetworkService.buildGraph                | 2 for-uri clasice (useri, apoi prietenii)| Repository.stream(...).forEach(...)
                                          | care populau graph                       | pentru fiecare dintre cele 2 surse
NetworkService.mostSociableCommunity     | for peste findComponents(graph),         | findComponents(graph).stream()
                                          | if length > bestLength -> best = ...     | .map(this::toCommunity)
                                          |                                           | .max(comparingInt(Community::longestPath))
NetworkService.findComponents (vecinii)  | for peste graph.get(current), if         | graph.get(current).stream()
                                          | visited.add -> queue.add                 | .filter(v -> !visited.contains(v))
                                          |                                           | .forEach(v -> {visited.add; queue.add;})
ConsoleUI.showUsers / showFriendships    | for (User u : ...) System.out.println(u) | service.getAllUsers().forEach(System.out::println)
FileRepository.loadData                  | BufferedReader + while(readLine())       | Files.lines(path) (try-with-resources)
                                          | + lineNumber incrementat manual          | + IntStream.range(0, linii.size())
FileRepository.writeToFile               | BufferedWriter + for (E e : findAll())   | Repository.stream(findAll()).map(entityToLine)
                                          | writer.write(...)                        | .collect(toList()); Files.write(path, linii)

- rezultatele se construiesc cu collect (toList, toSet, toCollection), nicio bucla nu mai
  adauga manual intr-o lista exterioara.
- sortarea comunitatilor dupa longestPath foloseste Comparator.comparingInt(Community::longestPath),
  fara compare scris de mana.

5. bucle pastrate (exceptii, justificate conform restrictiilor din enunt)
- NetworkService.findComponents: while (!queue.isEmpty()) ramane clasic. nu e o simpla
  parcurgere de colectie, ci un algoritm bfs cu stare mutabila (coada si multimea de vizitati
  care cresc pe masura ce se exploreaza graful); doar procesarea vecinilor fiecarui nod foloseste
  stream (filter + forEach), exact cum sugereaza enuntul.
- NetworkService.dfsLongest: for peste graph.get(current) ramane clasic, e backtracking
  (adaugare, recursie, apoi visited.remove la intoarcere). e exact exceptia acceptata explicit
  in enunt ("un algoritm de backtracking ... unde recursivitatea cu stream-uri devine greu de citit").

6. Optional (alegerea ceruta in enunt)
- Repository ramane cu findOne(id) care intoarce null daca nu exista (nu s-a schimbat in
  Optional<E>), ca sa nu fie nevoie sa se modifice toate implementarile (memory, file, db) si
  Javadoc-ul lor pentru un beneficiu mic, cata vreme nu exista alte cerinte noi in lab4.
- in schimb, service-ul (stratul care vorbeste cu ui) foloseste Optional peste tot unde o
  cautare poate sa nu gaseasca nimic: findUser/findUserByUsername fac
  Optional.ofNullable(repo.findOne(id)).orElseThrow(...), findByUsername intoarce direct
  Optional<User> (prin findFirst()), iar mostSociableCommunity intoarce Optional<Community>
  (din max() pe stream, ca si in lab3, dar acum e rezultatul lui max() si nu construit manual).
  nicaieri nu se apeleaza get() fara verificare.

7. comportament neschimbat
- fisierul lipsa, liniile corupte (raportate cu numarul liniei) si toate exceptiile se trateaza
  la fel ca la lab3; IOException din interiorul lambda-urilor Files.lines/Files.write e prinsa
  in try-with-resources / try-catch si impachetata tot in RepositoryException, ca inainte.
- SOLID si design patterns raman cele descrise la lab3 (SRP, OCP, LSP, DIP; Factory, Strategy,
  Decorator); Template Method a fost inlocuit cu composition (Function), motivat la punctul 2.

test data (neschimbate fata de lab3)
- users.txt: 10 users; friendships.txt: communities {1,3,4,5,6,7}, {8}, {9}, {10}, {11}
- at startup: 5 communities, the most sociable is {1,3,4,5,6,7} (longest path = 5)
- data/test_corupt/: files with corrupt lines, for demonstration
