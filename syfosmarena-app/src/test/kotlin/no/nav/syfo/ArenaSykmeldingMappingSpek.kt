package no.nav.syfo

import io.kotest.core.spec.style.FunSpec
import no.nav.syfo.arena.createArenaSykmelding
import no.nav.syfo.model.ReceivedSykmelding
import no.nav.syfo.model.SporsmalSvar
import no.nav.syfo.rules.RuleMetadata
import no.nav.syfo.rules.ValidationRuleChain
import no.nav.syfo.rules.executeFlow
import org.amshove.kluent.shouldBeEqualTo
import java.time.LocalDate
import java.time.LocalDateTime

class ArenaSykmeldingMappingSpek : FunSpec({

    context("Testing createArenaSykmelding") {
        test("Should check rule mapping of Arena sykmelding") {
            val healthInformation = generateSykmelding(
                perioder = listOf(
                    generatePeriode(
                        fom = LocalDate.now(),
                        tom = LocalDate.now().plusMonths(3).plusDays(1)
                    )
                )
            )

            val metadata = RuleMetadata(
                signatureDate = LocalDateTime.now(),
                receivedDate = LocalDateTime.now(),
                rulesetVersion = "1"
            )

            val receivedSykmelding = ReceivedSykmelding(
                sykmelding = healthInformation,
                personNrPasient = "123124",
                tlfPasient = "13214",
                personNrLege = "123145",
                navLogId = "0412",
                msgId = "12314-123124-43252-2344",
                legekontorOrgNr = "",
                legekontorHerId = "",
                legekontorReshId = "",
                legekontorOrgName = "Legevakt",
                mottattDato = LocalDateTime.now(),
                rulesetVersion = "",
                fellesformat = "",
                tssid = "",
                merknader = null,
                partnerreferanse = "",
                legeHelsepersonellkategori = null,
                legeHprNr = null,
                vedlegg = null,
                utenlandskSykmelding = null
            )

            val validationRuleChain = ValidationRuleChain.values().executeFlow(receivedSykmelding.sykmelding, metadata)
            val results = listOf(validationRuleChain).flatten()

            createArenaSykmelding(
                receivedSykmelding,
                results,
                "12355234"
            ).eiaDokumentInfo.dokumentInfo.ediLoggId shouldBeEqualTo receivedSykmelding.navLogId
        }

        test("Should check rule mapping of hendelseStatus") {
            val healthInformation = generateSykmelding(
                utdypendeOpplysninger = mapOf(
                    "6.1" to mapOf(
                        "6.1.1" to SporsmalSvar("Pasient syk?", "Tekst", listOf())
                    )
                )
            )

            val metadata = RuleMetadata(
                signatureDate = LocalDateTime.now(),
                receivedDate = LocalDateTime.now(),
                rulesetVersion = "1"
            )

            val receivedSykmelding = ReceivedSykmelding(
                sykmelding = healthInformation,
                personNrPasient = "123124",
                tlfPasient = "13214",
                personNrLege = "123145",
                navLogId = "0412",
                msgId = "12314-123124-43252-2344",
                legekontorOrgNr = "",
                legekontorHerId = "",
                legekontorReshId = "",
                legekontorOrgName = "Legevakt",
                mottattDato = LocalDateTime.now(),
                rulesetVersion = "",
                fellesformat = "",
                tssid = "",
                merknader = null,
                partnerreferanse = "",
                legeHelsepersonellkategori = null,
                legeHprNr = null,
                vedlegg = null,
                utenlandskSykmelding = null
            )

            val validationRuleChain = ValidationRuleChain.values().executeFlow(receivedSykmelding.sykmelding, metadata)
            val results = listOf(validationRuleChain).flatten()

            createArenaSykmelding(
                receivedSykmelding,
                results,
                "12355234"
            ).arenaHendelse.hendelse.first().hendelseStatus shouldBeEqualTo "UTFORT"
        }

        test("Should check mapping of legeFnr") {
            val healthInformation = generateSykmelding(
                utdypendeOpplysninger = mapOf(
                    "6.1" to mapOf(
                        "6.1.1" to SporsmalSvar("Pasient syk?", "Tekst", listOf())
                    )
                )
            )

            val metadata = RuleMetadata(
                signatureDate = LocalDateTime.now(),
                receivedDate = LocalDateTime.now(),
                rulesetVersion = "1"
            )

            val receivedSykmelding = ReceivedSykmelding(
                sykmelding = healthInformation,
                personNrPasient = "123124",
                tlfPasient = "13214",
                personNrLege = "123145",
                navLogId = "0412",
                msgId = "12314-123124-43252-2344",
                legekontorOrgNr = "",
                legekontorHerId = "",
                legekontorReshId = "",
                legekontorOrgName = "Legevakt",
                mottattDato = LocalDateTime.now(),
                rulesetVersion = "",
                fellesformat = "",
                tssid = "",
                merknader = null,
                partnerreferanse = "",
                legeHelsepersonellkategori = null,
                legeHprNr = null,
                vedlegg = null,
                utenlandskSykmelding = null
            )

            val validationRuleChain = ValidationRuleChain.values().executeFlow(receivedSykmelding.sykmelding, metadata)
            val results = listOf(validationRuleChain).flatten()

            createArenaSykmelding(
                receivedSykmelding,
                results,
                "12355234"
            ).eiaDokumentInfo.avsender.lege.legeFnr.toString() shouldBeEqualTo "123145"
        }
    }
})
