package io.mojaloop.core.accounting.domain.query;

import io.mojaloop.component.jpa.routing.annotation.Read;
import io.mojaloop.core.accounting.contract.data.ChartData;
import io.mojaloop.core.accounting.contract.exception.chart.ChartIdNotFoundException;
import io.mojaloop.core.accounting.contract.query.ChartQuery;
import io.mojaloop.core.accounting.domain.model.Chart;
import io.mojaloop.core.accounting.domain.repository.ChartRepository;
import io.mojaloop.core.common.datatype.identifier.accounting.ChartId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ChartQueryHandler implements ChartQuery {

    private final ChartRepository chartRepository;

    public ChartQueryHandler(final ChartRepository chartRepository) {

        assert chartRepository != null;

        this.chartRepository = chartRepository;
    }

    @Transactional(readOnly = true)
    @Read
    @Override
    public ChartData get(final ChartId chartId) throws ChartIdNotFoundException {

        return this.chartRepository.findById(chartId).orElseThrow(() -> new ChartIdNotFoundException(chartId)).convert();
    }

    @Transactional(readOnly = true)
    @Read
    @Override
    public List<ChartData> getAll() {

        return this.chartRepository.findAll().stream().map(Chart::convert).toList();
    }

    @Transactional(readOnly = true)
    @Read
    @Override
    public List<ChartData> getByNameContains(final String name) {

        return this.chartRepository.findAll(ChartRepository.Filters.withNameContains(name)).stream().map(Chart::convert).toList();
    }

}
