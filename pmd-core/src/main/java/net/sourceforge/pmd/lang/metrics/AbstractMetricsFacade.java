/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.metrics;

import java.util.Objects;

import net.sourceforge.pmd.lang.ast.QualifiableNode;
import net.sourceforge.pmd.lang.metrics.Metric.Version;

/**
 * Base class for a façade that can compute metrics for types, operations and compute aggregate results with a result
 * option.
 *
 * @param <T> Type of type declaration nodes of the language
 * @param <O> Type of operation declaration nodes of the language
 *
 * @author Clément Fournier
 */
public abstract class AbstractMetricsFacade<T extends QualifiableNode, O extends QualifiableNode> {


    /**
     * Gets the language specific metrics computer.
     *
     * @return The metrics computer
     */
    protected abstract MetricsComputer<T, O> getLanguageSpecificComputer();


    /**
     * Gets the language-specific project memoizer.
     *
     * @return The project memoizer
     */
    protected abstract ProjectMemoizer<T, O> getLanguageSpecificProjectMemoizer();


    /**
     * Computes a metric identified by its code on a class AST node, possibly selecting a variant with the {@code
     * MetricVersion} parameter.
     *
     * @param key     The key identifying the metric to be computed
     * @param node    The node on which to compute the metric
     * @param version The version of the metric
     *
     * @return The value of the metric, or {@code Double.NaN} if the value couln't be computed
     */
    public double computeForType(MetricKey<T> key, T node, MetricVersion version) {

        Objects.requireNonNull(key, "The metric key must not be null");

        if (!key.supports(node)) {
            return Double.NaN;
        }

        MetricVersion safeVersion = (version == null) ? Version.STANDARD : version;
        MetricMemoizer<T> memoizer = getLanguageSpecificProjectMemoizer().getClassMemoizer(node.getQualifiedName());

        return memoizer == null ? Double.NaN
                                : getLanguageSpecificComputer().computeForType(key, node, false, safeVersion, memoizer);
    }


    /**
     * Computes a metric identified by its key on a operation AST node.
     *
     * @param key     The key identifying the metric to be computed
     * @param node    The node on which to compute the metric
     * @param version The version of the metric
     *
     * @return The value of the metric, or {@code Double.NaN} if the value couln't be computed
     */
    public double computeForOperation(MetricKey<O> key, O node,
                                      MetricVersion version) {

        Objects.requireNonNull(key, "The metric key must not be null");

        if (!key.supports(node)) {
            return Double.NaN;
        }

        MetricVersion safeVersion = (version == null) ? Version.STANDARD : version;
        MetricMemoizer<O> memoizer = getLanguageSpecificProjectMemoizer().getOperationMemoizer(node.getQualifiedName());

        return memoizer == null ? Double.NaN
                                : getLanguageSpecificComputer().computeForOperation(key, node, false,
                                                                                    safeVersion, memoizer);

    }


    /**
     * Compute the sum, average, or highest value of the operation metric on all operations of the class node. The type
     * of operation is specified by the {@link ResultOption} parameter.
     *
     * @param key     The key identifying the metric to be computed
     * @param node    The node on which to compute the metric
     * @param version The version of the metric
     * @param option  The result option to use
     *
     * @return The value of the metric, or {@code Double.NaN} if the value couln't be computed or {@code option} is
     * {@code null}
     */
    public double computeWithResultOption(MetricKey<O> key, T node,
                                          MetricVersion version, ResultOption option) {

        Objects.requireNonNull(key, "The metric key must not be null");
        Objects.requireNonNull(option, "The result option must not be null");

        MetricVersion safeVersion = (version == null) ? Version.STANDARD : version;

        return getLanguageSpecificComputer().computeWithResultOption(key, node, false, safeVersion,
                                                                     option, getLanguageSpecificProjectMemoizer());
    }
}
