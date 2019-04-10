package no.nav.syfo.arena

import no.nav.helse.arenaSykemelding.ArenaSykmelding
import no.nav.helse.arenaSykemelding.EiaDokumentInfoType
import no.nav.helse.arenaSykemelding.HendelseType
import no.nav.helse.arenaSykemelding.LegeType
import no.nav.helse.arenaSykemelding.MerknadType
import no.nav.helse.arenaSykemelding.PasientDataType
import no.nav.helse.arenaSykemelding.PersonType
import no.nav.syfo.model.ReceivedSykmelding
import no.nav.syfo.rules.Rule

fun createArenaSykmelding(receivedSykmelding: ReceivedSykmelding, ruleResults: List<Rule<Any>>, journalpostid: String): ArenaSykmelding = ArenaSykmelding().apply {
    eiaDokumentInfo = EiaDokumentInfoType().apply {
        dokumentInfo = no.nav.helse.arenaSykemelding.DokumentInfoType().apply {
            dokumentType = "SM2"
            dokumentTypeVersjon = "1.0"
            dokumentreferanse = receivedSykmelding.msgId
            ediLoggId = receivedSykmelding.navLogId
            journalReferanse = journalpostid
            dokumentDato = receivedSykmelding.mottattDato
        }
        behandlingInfo = EiaDokumentInfoType.BehandlingInfo().apply {
            ruleResults.onEach {
                merknad.add(it.toMerknad())
            }
        }
        avsender = EiaDokumentInfoType.Avsender().apply {
            LegeType().apply {
                legeFnr = receivedSykmelding.personNrLege
            }
        }
        avsenderSystem = EiaDokumentInfoType.AvsenderSystem().apply {
            systemNavn = "EIA"
            systemVersjon = "1.0.0"
        }
    }
    arenaHendelse = ArenaSykmelding.ArenaHendelse().apply {
        ruleResults.onEach {
            hendelse.add(it.toHendelse())
        }
    }
    pasientData = PasientDataType().apply {
        person = PersonType().apply {
            personFnr = receivedSykmelding.personNrPasient
        }
    }
    foersteFravaersdag = receivedSykmelding.sykmelding.kontaktMedPasient.kontaktDato
    identDato = receivedSykmelding.sykmelding.behandletTidspunkt.toLocalDate()
}

fun Rule<Any>.toMerknad() = MerknadType().apply {
    merknadNr = ruleId.toString()
    merknadType = "2"
    merknadBeskrivelse = name
}

fun Rule<Any>.toHendelse() = HendelseType().apply {
    hendelsesTypeKode = arenaHendelseType.type
    meldingFraLege = meldingFraLege
    hendelseStatus = arenaHendelseStatus.type
    hendelseTekst = hendelseTekst
}
