# ApplicationsManager

1. Zrezygnowano z utworzenia relacji 1:n dla Aplikacji i Historii. Utrudnia to usuwanie aplikacji bez usuwania historii.
 Id Aplikacji w Historii jest przechowywane jako pole klasy.
  Zachowując relację należałoby robić update dla pola application w Historii i ustawiać id na null.
 
2. Sterowanie wszystkimi statusami odbywa się za pomocą 1 usługi (w tym Rejeceted i Deleted). Usługa przyjmuje 2 wartość Status oraz Reason. 
   Status domyślnie powinien być wysyłany przez np. wybór z listy typu drop down. Reason może być pusty, jest brany pod uwagę tylko przy statusach podanych w wymaganiach. 

3. Usunąć aplikację można tylko jeśli jest w statusie Deleted. To nie musi się odbywać ręcznie, można np: ustawić scheduler na usuwanie aplikacji w statusie Deleted,
    które mają datę updatedAt starszą niż 7 dni.
    
4. Przykładowe zapytania do usług znajdują się w pakiecie http.
