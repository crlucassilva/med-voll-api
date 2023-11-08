package med.voll.api.domain.consulta;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import med.voll.api.domain.medico.Especialidade;

import java.time.LocalDateTime;

public record DadosAgendamentoConsulta(

        @JsonAlias("id_medico")
        Long idMedico,

        @NotNull
        @JsonAlias("id_paciente")
        Long idPaciente,

        @NotNull
        @Future
        @JsonFormat(pattern = "dd/MM/yyy HH:mm")
        LocalDateTime data,

        Especialidade especialidade) {
}
