-- phpMyAdmin SQL Dump
-- version 4.7.4
-- https://www.phpmyadmin.net/
--
-- Hostiteľ: 127.0.0.1
-- Čas generovania: So 22.Dec 2018, 16:43
-- Verzia serveru: 10.1.28-MariaDB
-- Verzia PHP: 7.1.10

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET AUTOCOMMIT = 0;
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Databáza: `obchod`
--

-- --------------------------------------------------------

--
-- Štruktúra tabuľky pre tabuľku `kosik`
--

CREATE TABLE `kosik` (
  `ID` int(11) NOT NULL,
  `ID_pouzivatela` int(11) NOT NULL,
  `ID_tovaru` int(11) NOT NULL,
  `cena` int(11) NOT NULL,
  `ks` int(11) NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Štruktúra tabuľky pre tabuľku `obj_polozky`
--

CREATE TABLE `obj_polozky` (
  `ID` int(11) NOT NULL,
  `obj_cislo` varchar(25) NOT NULL,
  `ID_tovaru` int(11) NOT NULL,
  `cena` int(11) NOT NULL,
  `ks` int(11) NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Štruktúra tabuľky pre tabuľku `obj_zoznam`
--

CREATE TABLE `obj_zoznam` (
  `ID` int(11) NOT NULL,
  `obj_cislo` varchar(25) NOT NULL,
  `datum_objednavky` date NOT NULL,
  `ID_pouzivatela` int(11) NOT NULL,
  `suma` double NOT NULL,
  `stav` varchar(20) NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Štruktúra tabuľky pre tabuľku `pouzivatelia`
--

CREATE TABLE `pouzivatelia` (
  `ID` int(11) NOT NULL,
  `login` varchar(20) CHARACTER SET utf8 COLLATE utf8_slovak_ci NOT NULL,
  `heslo` varchar(20) CHARACTER SET utf8 COLLATE utf8_slovak_ci NOT NULL,
  `mail` varchar(40) CHARACTER SET utf8 COLLATE utf8_slovak_ci NOT NULL,
  `adresa` varchar(50) CHARACTER SET utf8 COLLATE utf8_slovak_ci NOT NULL,
  `zlava` int(11) NOT NULL,
  `meno` varchar(20) CHARACTER SET utf8 COLLATE utf8_slovak_ci NOT NULL,
  `priezvisko` varchar(20) CHARACTER SET utf8 COLLATE utf8_slovak_ci NOT NULL,
  `poznamky` varchar(15) CHARACTER SET utf8 COLLATE utf8_slovak_ci NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1;


-- --------------------------------------------------------

--
-- Štruktúra tabuľky pre tabuľku `sklad`
--

CREATE TABLE `sklad` (
  `ID` int(11) NOT NULL,
  `nazov` varchar(30) CHARACTER SET utf8 COLLATE utf8_slovak_ci NOT NULL,
  `ks` int(11) NOT NULL,
  `cena` int(11) NOT NULL,
  `obrazok` varchar(30) NOT NULL,
  `poznamky` text NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

--
-- Kľúče pre exportované tabuľky
--

--
-- Indexy pre tabuľku `kosik`
--
ALTER TABLE `kosik`
  ADD PRIMARY KEY (`ID`);

--
-- Indexy pre tabuľku `obj_polozky`
--
ALTER TABLE `obj_polozky`
  ADD PRIMARY KEY (`ID`);

--
-- Indexy pre tabuľku `obj_zoznam`
--
ALTER TABLE `obj_zoznam`
  ADD PRIMARY KEY (`ID`);

--
-- Indexy pre tabuľku `pouzivatelia`
--
ALTER TABLE `pouzivatelia`
  ADD PRIMARY KEY (`ID`);

--
-- Indexy pre tabuľku `sklad`
--
ALTER TABLE `sklad`
  ADD PRIMARY KEY (`ID`);

--
-- AUTO_INCREMENT pre exportované tabuľky
--

--
-- AUTO_INCREMENT pre tabuľku `kosik`
--
ALTER TABLE `kosik`
  MODIFY `ID` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=61;

--
-- AUTO_INCREMENT pre tabuľku `obj_polozky`
--
ALTER TABLE `obj_polozky`
  MODIFY `ID` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=83;

--
-- AUTO_INCREMENT pre tabuľku `obj_zoznam`
--
ALTER TABLE `obj_zoznam`
  MODIFY `ID` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=13;

--
-- AUTO_INCREMENT pre tabuľku `pouzivatelia`
--
ALTER TABLE `pouzivatelia`
  MODIFY `ID` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=33;

--
-- AUTO_INCREMENT pre tabuľku `sklad`
--
ALTER TABLE `sklad`
  MODIFY `ID` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=9;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
