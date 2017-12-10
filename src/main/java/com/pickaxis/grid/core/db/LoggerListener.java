/* 
 * Grid
 * Copyright (c) 2017 PickAxis, All Rights Resered.
 * 
 * NOTICE:  All information contained herein is, and remains the property of PickAxis.  The intellectual
 * and technical concepts contained herein are proprietary to PickAxis and may be covered by U.S. and
 * Foreign Patents, patents in process, and are protected by trade secret or copyright law.  Dissemination
 * of this information or reproduction of this material is strictly forbidden unless prior written permission
 * is obtained from PickAxis.  Use of this source code or any derivative of it is strictly forbidden.
 */

package com.pickaxis.grid.core.db;

import com.pickaxis.grid.core.GridPlugin;
import java.util.Arrays;
import java.util.logging.Level;
import org.jooq.Configuration;
import org.jooq.ExecuteContext;
import org.jooq.ExecuteType;
import org.jooq.Param;
import org.jooq.QueryPart;
import org.jooq.VisitContext;
import org.jooq.VisitListener;
import org.jooq.VisitListenerProvider;
import org.jooq.impl.DSL;
import org.jooq.impl.DefaultExecuteListener;
import org.jooq.impl.DefaultVisitListener;
import org.jooq.impl.DefaultVisitListenerProvider;
import org.jooq.tools.JooqLogger;
import org.jooq.tools.StringUtils;

import static org.jooq.impl.DSL.val;
import static org.jooq.tools.StringUtils.abbreviate;

/**
 * A default {@link LoggerListener} that just logs events to java.util.logging,
 * log4j, or slf4j using the {@link JooqLogger}
 *
 * @author Lukas Eder
 */
public class LoggerListener extends DefaultExecuteListener {
    @Override
    public void renderEnd(ExecuteContext ctx) {
        Configuration configuration = abbreviateBindVariables(ctx.configuration());

        String[] batchSQL = ctx.batchSQL();
        if (ctx.query() != null) {

            // Actual SQL passed to JDBC
            GridPlugin.getInstance().getLogger().log(Level.FINER, "Executing query {0}", ctx.sql());

            // [#1278] DEBUG log also SQL with inlined bind values, if
            // that is not the same as the actual SQL passed to JDBC
            String inlined = DSL.using(configuration).renderInlined(ctx.query());
            if (!ctx.sql().equals(inlined)) {
                GridPlugin.getInstance().getLogger().log(Level.FINER, "-> with bind values {0}", inlined);
            }
        }

        // [#2987] Log routines
        else if (ctx.routine() != null) {
            GridPlugin.getInstance().getLogger().log(Level.FINER, "Calling routine {0}", ctx.sql());

            String inlined = DSL.using(configuration)
                                .renderInlined(ctx.routine());

            if (!ctx.sql().equals(inlined)) {
                GridPlugin.getInstance().getLogger().log(Level.FINER, "-> with bind values {0}", inlined);
            }
        }

        else if (!StringUtils.isBlank(ctx.sql())) {

            // [#1529] Batch queries should be logged specially
            if (ctx.type() == ExecuteType.BATCH) {
                GridPlugin.getInstance().getLogger().log(Level.FINER, "Executing batch query {0}", ctx.sql());
            }
            else {
                GridPlugin.getInstance().getLogger().log(Level.FINER, "Executing query {0}", ctx.sql());
            }
        }

        // [#2532] Log a complete BatchMultiple query
        else if (batchSQL.length > 0) {
            if (batchSQL[batchSQL.length - 1] != null) {
                for (String sql : batchSQL) {
                    GridPlugin.getInstance().getLogger().log(Level.FINER, "Executing batch query {0}", sql);
                }
            }
        }
    }

    @Override
    public void executeEnd(ExecuteContext ctx) {
        if (ctx.rows() >= 0) {
            GridPlugin.getInstance().getLogger().log(Level.FINER, "Affected row(s) {0}", ctx.rows());
        }
    }

    private static final int maxLength = 2000;

    /**
     * Add a {@link VisitListener} that transforms all bind variables by abbreviating them.
     */
    private final Configuration abbreviateBindVariables(Configuration configuration) {
        VisitListenerProvider[] oldProviders = configuration.visitListenerProviders();
        VisitListenerProvider[] newProviders = new VisitListenerProvider[oldProviders.length + 1];
        System.arraycopy(oldProviders, 0, newProviders, 0, oldProviders.length);
        newProviders[newProviders.length - 1] = new DefaultVisitListenerProvider(new BindValueAbbreviator());

        return configuration.derive(newProviders);
    }

    private static class BindValueAbbreviator extends DefaultVisitListener {

        private boolean anyAbbreviations = false;

        @Override
        public void visitStart(VisitContext context) {
            if (context.renderContext() != null) {
                QueryPart part = context.queryPart();

                if (part instanceof Param<?>) {
                    Param<?> param = (Param<?>) part;
                    Object value = param.getValue();

                    if (value instanceof String && ((String) value).length() > maxLength) {
                        anyAbbreviations = true;
                        context.queryPart(val(abbreviate((String) value, maxLength)));
                    }
                    else if (value instanceof byte[] && ((byte[]) value).length > maxLength) {
                        anyAbbreviations = true;
                        context.queryPart(val(Arrays.copyOf((byte[]) value, maxLength)));
                    }
                }
            }
        }

        @Override
        public void visitEnd(VisitContext context) {
            if (anyAbbreviations) {
                if (context.queryPartsLength() == 1) {
                    context.renderContext().sql(" -- Bind values may have been abbreviated for DEBUG logging. Use TRACE logging for very large bind variables.");
                }
            }
        }
    }
}
