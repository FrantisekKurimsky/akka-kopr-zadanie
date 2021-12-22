# Zadanie – distribuovaný program

## Firma Station vyrába systém pre železničnú stanicu.

Na železničnú stanicu prichádzajú pravidelne vlaky pre prepravu cestujúcich, pričom každý z nich má svoj vlastný typ (R – rýchlik, Os – osobný a pod.), a číselné označenie (napr. 602). Vlak prichádza v konkrétny deň (16. 11. 2021), presnú hodinu a minútu (16:02) na konkrétne nástupište (5. nástupište). V inú presnú hodinu a minútu (16:05) smeruje do konkrétneho cieľa (Poprad-Tatry).

Prirodzene, vlaky meškajú a nezriedka vlak R602 príde o 16:12 namiesto 16:02 a odíde o 16:20 namiesto pôvodného 16:05.

Navrhnite informačný systém, ktorý má podporovať nasledovné služby

- Evidenciu reálneho času príchodu konkrétneho vlaku v daný deň
- Evidenciu reálneho času odchodu konkrétneho vlaku v daný deň
- Možnosť zaevidovať plánované meškanie príchodu konkrétneho vlaku pre účely elektronických tabúľ a hlasového systému („Vlak R602 bude meškať 10 minút“)
- Výpis 20 odchádzajúcich vlakov krížom cez všetky nástupištia pre účely centrálnej elektronickej tabule
    - R602 16:02 Poprad Tatry, 2. nástupište, 10 m.
    - Os122 16:05 Prešov, 5. nástupište

- Výpis prehľadu odchodov a príchodov konkrétneho vlaku, vrátane meškaní, za konkrétne časové obdobie.
    - 12.6.2021 R602 16:02 Poprad Tatry, 2. nástupište, 10 m.
    - 13.6.2021 R602 16:02 Poprad Tatry, 2. nástupište, 12 m.
    - 14.6.2021 R602 16:02 Poprad Tatry, 2. nástupište, 0 m.
    - 15.6.2021 R602 16:02 Poprad Tatry, 2. nástupište, 25 m.

# III. Akka aktor

### Implementujte zadanie, ktoré podporuje len nasledovné vlastnosti:

   - evidencia príchodu. Prijmite správu, ktorá hovorí, ktorý vlak, v ktorý dátum a čas dorazil na konkrétne nástupište.
   - evidencia odchodu. Prijmite správu, ktorá hovorí, ktorý vlak, v ktorý dátum a čas odišiel z konkrétneho nástupišta
   - evidencia meškania. Umožnite sledovať meškania vlakov
   - výpis najbližších 20 odchádzajúcich vlakov krížom cez všetky nástupištia pre účely centrálnej elektronickej tabule

Pri demonštrácii predpokladajte, že na stanici existuje 5 elektronických tabúľ, ktoré nezávisle od seba ukazujú informácie. Tabule sa môžu kaziť – preukážte to v kóde.

Použite jedného hlavného aktora, ktorý bude prijímať príkazy zodpovedajúce jednotlivým službám a výsledky bude vracať do mimoaktorového sveta primeraným spôsobom.
![img.png](img.png)