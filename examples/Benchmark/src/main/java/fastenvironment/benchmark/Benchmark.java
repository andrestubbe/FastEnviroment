package fastenvironment.benchmark;

import fastenvironment.FastEnvironment;
import fastenvironment.LanguageInfo;
import fastenvironment.RegionalInfo;
import org.openjdk.jmh.annotations.*;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@State(Scope.Benchmark)
@Warmup(iterations = 2, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 3, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(1)
public class Benchmark {

    @org.openjdk.jmh.annotations.Benchmark
    public LanguageInfo benchmarkGetUILanguage() {
        return FastEnvironment.getUILanguage();
    }

    @org.openjdk.jmh.annotations.Benchmark
    public RegionalInfo benchmarkGetRegionalInfo() {
        return FastEnvironment.getRegionalInfo();
    }

    @org.openjdk.jmh.annotations.Benchmark
    public long benchmarkGetKeyboardLayout() {
        return FastEnvironment.getKeyboardLayout();
    }

    @org.openjdk.jmh.annotations.Benchmark
    public String benchmarkStandardJvmLocale() {
        return Locale.getDefault().toLanguageTag();
    }
}
