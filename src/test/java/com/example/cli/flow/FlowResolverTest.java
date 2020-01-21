package com.example.cli.flow;

import com.example.cli.model.CliCommand;
import com.example.core_modules.model.log.LogModel;
import com.example.core_modules.writer.ReportGenerator;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyString;
import static org.powermock.api.mockito.PowerMockito.*;

@RunWith(PowerMockRunner.class)
public class FlowResolverTest {

    private FlowResolver flowResolver;
    private Analyzer analyzer = mock(Analyzer.class);
    private ReportGenerator generator = mock(ReportGenerator.class);

    private Map<CliCommand, String> commandsToExecute;

    @Before
    public void setUp() {
        this.commandsToExecute = new HashMap<>();
        this.flowResolver = new FlowResolver();
        Whitebox.setInternalState(flowResolver, "analyzer", analyzer);
        Whitebox.setInternalState(flowResolver, "reportGenerator", generator);
    }

    @Test
    public void resolve_pathAndReport_shouldRun() {
        Set<LogModel> emptySet = new HashSet<>();
        this.commandsToExecute.put(CliCommand.DIRECTORY_PATH, "/var/test");
        this.commandsToExecute.put(CliCommand.REPORT, null);
        when(analyzer.analyzeWithoutTimeSpecified(anyString(), anyBoolean())).thenReturn(new HashMap<String, LogModel>());
        doNothing().when(generator).generateReport(emptySet, false);

        flowResolver.resolve(this.commandsToExecute);
        Mockito.verify(analyzer, Mockito.times(1))
                .analyzeWithoutTimeSpecified(
                        this.commandsToExecute.get(CliCommand.DIRECTORY_PATH),
                        false);
    }

    @Test
    public void resolve_pathAndDateToAndReport_shouldRun() {
        Set<LogModel> emptySet = new HashSet<>();
        this.commandsToExecute.put(CliCommand.DIRECTORY_PATH, "/var/test");
        this.commandsToExecute.put(CliCommand.DATE_TO, "20200101120000");
        this.commandsToExecute.put(CliCommand.REPORT, null);
        when(analyzer.analyzeWithTimeSpecified(anyString(), anyString(), anyString(), anyBoolean())).thenReturn(new HashMap<String, LogModel>());
        doNothing().when(generator).generateReport(emptySet, false);

        flowResolver.resolve(this.commandsToExecute);
        Mockito.verify(analyzer, Mockito.times(1))
                .analyzeWithTimeSpecified(
                        this.commandsToExecute.get(CliCommand.DIRECTORY_PATH),
                        null,
                        this.commandsToExecute.get(CliCommand.DATE_TO),
                        false);
    }

    @Test
    public void resolve_pathAndDateFromAndReport_shouldRun() {
        Set<LogModel> emptySet = new HashSet<>();
        this.commandsToExecute.put(CliCommand.DIRECTORY_PATH, "/var/test");
        this.commandsToExecute.put(CliCommand.DATE_FROM, "20200101120000");
        this.commandsToExecute.put(CliCommand.REPORT, null);
        when(analyzer.analyzeWithoutTimeSpecified(anyString(), anyBoolean())).thenReturn(new HashMap<String, LogModel>());
        doNothing().when(generator).generateReport(emptySet, false);

        flowResolver.resolve(this.commandsToExecute);

        Mockito.verify(analyzer, Mockito.times(1))
                .analyzeWithTimeSpecified(
                        this.commandsToExecute.get(CliCommand.DIRECTORY_PATH),
                        this.commandsToExecute.get(CliCommand.DATE_FROM),
                        null,
                        false);
    }

    @Test
    public void resolve_pathAndDateFromAnaDateToAndReport_shouldRun() {
        Set<LogModel> emptySet = new HashSet<>();
        this.commandsToExecute.put(CliCommand.DIRECTORY_PATH, "/var/test");
        this.commandsToExecute.put(CliCommand.DATE_FROM, "20200101120000");
        this.commandsToExecute.put(CliCommand.DATE_TO, "20200101160000");
        this.commandsToExecute.put(CliCommand.REPORT, null);
        when(analyzer.analyzeWithoutTimeSpecified(anyString(), anyBoolean())).thenReturn(new HashMap<String, LogModel>());
        doNothing().when(generator).generateReport(emptySet, false);

        flowResolver.resolve(this.commandsToExecute);

        Mockito.verify(analyzer, Mockito.times(1))
                .analyzeWithTimeSpecified(
                        this.commandsToExecute.get(CliCommand.DIRECTORY_PATH),
                        this.commandsToExecute.get(CliCommand.DATE_FROM),
                        this.commandsToExecute.get(CliCommand.DATE_TO),
                        false);
    }

    @Test
    public void resolve_report_shouldRun() {
        Set<LogModel> emptySet = new HashSet<>();
        this.commandsToExecute.put(CliCommand.REPORT, null);
        doNothing().when(generator).generateReport(emptySet, false);

        flowResolver.resolve(this.commandsToExecute);
        Mockito.verify(generator, Mockito.times(1))
                .generateReportFromHistoryFile(false);
    }
}