package br.com.astro.atendimentos.service.Impl;
import br.com.astro.atendimentos.dto.request.AtendimentoRequestDTO;
import br.com.astro.atendimentos.dto.response.AtendimentoResponseDTO;
import br.com.astro.atendimentos.entity.Atendimento;
import br.com.astro.atendimentos.enums.Status;
import br.com.astro.atendimentos.exceptions.AtendimentoNaoEncontradoException;
import br.com.astro.atendimentos.repository.AtendimentoRepository;
import br.com.astro.atendimentos.service.AtendimentoService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

@Service
public class AtendimentoServiceImpl implements AtendimentoService {

    private final AtendimentoRepository repository;

    public AtendimentoServiceImpl(AtendimentoRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional
    public AtendimentoResponseDTO criar(AtendimentoRequestDTO dto) {
        Atendimento atendimento = new Atendimento();
        atendimento.setNomeDoPaciente(dto.nomeDoPaciente());
        atendimento.setNomeDoEspecialista(dto.nomeDoEspecialista());
        atendimento.setDataDoAtendimento(dto.dataDoAtendimento());
        atendimento.setStatusAtendimento(Status.AGUARDANDO);
        return toResponse(repository.save(atendimento));
    }

    @Override
    @Transactional(readOnly = true)
    public AtendimentoResponseDTO buscarPorId(String id) {
        return toResponse(buscarEntidade(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AtendimentoResponseDTO> listar(Status status, Pageable pageable) {
        Page<Atendimento> page = (status != null)
                ? repository.findByStatusAtendimento(status, pageable)
                : repository.findAll(pageable);
        return page.map(this::toResponse);
    }

    @Override
    @Transactional
    public AtendimentoResponseDTO atualizarStatus(String id, Status novoStatus) {
        Atendimento atendimento = buscarEntidade(id);
        atendimento.setStatusAtendimento(novoStatus);
        return toResponse(repository.save(atendimento));
    }

    @Override
    @Transactional
    public AtendimentoResponseDTO cancelar(String id) {
        return atualizarStatus(id, Status.CANCELADO);
    }

    private Atendimento buscarEntidade(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new AtendimentoNaoEncontradoException("Atendimento não encontrado com o ID: " + id));
    }

    private AtendimentoResponseDTO toResponse(Atendimento a) {
        return new AtendimentoResponseDTO(
                a.getId(),
                a.getNomeDoPaciente(),
                a.getNomeDoEspecialista(),
                a.getDataDoAtendimento(),
                a.getStatusAtendimento()
        );
    }
}