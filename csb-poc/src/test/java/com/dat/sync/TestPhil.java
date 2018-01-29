/*
 * $Id: $
 *
 * Copyright (C) 2018, TransCore LP. All Rights Reserved
 */
package com.dat.sync;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TestPhil
{
    final Map<Integer, Map<String, Integer>> m_companies = new HashMap<>(1_000);
    final List<String> m_dates = new ArrayList<>();

    public static void main(final String[] args)
    {
        final TestPhil p = new TestPhil(args);
        try
        {
            p.readData();
            p.outputData();
        }
        catch (final Exception e)
        {
            e.printStackTrace();
        }
    }

    public TestPhil(final String[] args)
    {}

    private void readData() throws Exception
    {
        final Path path = Paths.get("C:/cygwin64/usr/tmp", "vansByCompany.txt");
        Files.lines(path).forEach(s -> processLine(s));

        System.out.printf("Unique companies: %d\n", m_companies.size());
    }

    final Pattern LINE = Pattern.compile("^(.+)\\W([0-9]+)\\W([0-9]+)$");

    private void processLine(final String line)
    {
        final Matcher m = LINE.matcher(line);

        if (!m.matches())
        {
            throw new IllegalArgumentException(String.format("Illegal line: '%s'", line));
        }

        final String date = m.group(1);
        final int companyId = Integer.parseInt(m.group(2));
        final int count = Integer.parseInt(m.group(3));

        System.out.printf("%s %6d %6d\n", date, companyId, count);

        if (! m_dates.contains(date))
        {
            m_dates.add(date);
        }

        Map<String, Integer> dayTotal = m_companies.get(companyId);
        if (dayTotal == null)
        {
            dayTotal = new TreeMap<>();
        }
        dayTotal.put(date, count);

        m_companies.put(companyId, dayTotal);
    }

    private void outputData()
    {
        final StringBuilder sb = new StringBuilder(256);

        final Consumer<Integer> outputCompany = (companyId) ->
        {
            sb.setLength(0);
            sb.append(companyId);
            final Map<String, Integer> counts = m_companies.get(companyId);
            m_dates.stream().forEach(s -> sb.append(",").append(counts.get(s) == null ? 0 : counts.get(s)));
            System.out.println(sb.toString());
        };

        // output header
        sb.append("CompanyId");
        m_dates.stream().forEach(s -> sb.append(",").append(s));
        System.out.println(sb.toString());

        // Output data
        m_companies.keySet().stream().forEach(outputCompany);
    }
}
