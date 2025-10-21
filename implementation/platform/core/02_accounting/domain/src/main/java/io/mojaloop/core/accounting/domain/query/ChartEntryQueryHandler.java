package io.mojaloop.core.accounting.domain.query;

import io.mojaloop.component.jpa.routing.annotation.Read;
import io.mojaloop.core.accounting.contract.data.ChartEntryData;
import io.mojaloop.core.accounting.contract.exception.chart.ChartEntryIdNotFoundException;
import io.mojaloop.core.accounting.contract.query.ChartEntryQuery;
import io.mojaloop.core.accounting.domain.model.ChartEntry;
import io.mojaloop.core.accounting.domain.repository.ChartEntryRepository;
import io.mojaloop.core.common.datatype.identifier.accounting.ChartEntryId;
import io.mojaloop.core.common.datatype.identifier.accounting.ChartId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ChartEntryQueryHandler implements ChartEntryQuery {

    private final ChartEntryRepository chartEntryRepository;

    public ChartEntryQueryHandler(final ChartEntryRepository chartEntryRepository) {

        assert chartEntryRepository != null;

        this.chartEntryRepository = chartEntryRepository;
    }

    @Transactional(readOnly = true)
    @Read
    @Override
    public ChartEntryData get(final ChartEntryId chartEntryId) throws ChartEntryIdNotFoundException {

        return this.chartEntryRepository
                   .findById(chartEntryId)
                   .orElseThrow(() -> new ChartEntryIdNotFoundException(chartEntryId))
                   .convert();
    }

    @Transactional(readOnly = true)
    @Read
    @Override
    public List<ChartEntryData> get(final ChartId chartId) {

        return this.chartEntryRepository
                   .findAll(ChartEntryRepository.Filters.withChartId(chartId))
                   .stream()
                   .map(ChartEntry::convert)
                   .toList();
    }

    @Transactional(readOnly = true)
    @Read
    @Override
    public List<ChartEntryData> getAll() {

        return this.chartEntryRepository.findAll().stream().map(ChartEntry::convert).toList();
    }

    @Transactional(readOnly = true)
    @Read
    @Override
    public List<ChartEntryData> getByNameContains(final String name) {

        return this.chartEntryRepository
                   .findAll(ChartEntryRepository.Filters.withNameContains(name))
                   .stream()
                   .map(ChartEntry::convert)
                   .toList();
    }

}
