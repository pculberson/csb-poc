/*
 * $Id: $
 *
 * Copyright (C) 2017, TransCore LP. All Rights Reserved
 */
package com.dat.domain;

/**
 * Exception class for use in generated domain objects.
 *
 * @author Aaron Dunlop
 * @since 12/2003
 *
 * @version $Revision: 3478 $ $Date: 2005-08-03 16:00:51 -0700 (Wed, 03 Aug 2005) $ $Author: aarond $
 */
public class DomainException extends Exception
{
    private static final long serialVersionUID = 1L;

    public DomainException()
    {
        super();
    }

    public DomainException(final String message)
    {
        super(message);
    }

    public DomainException(final Throwable cause)
    {
        super(cause);
    }

    public DomainException(final String message, final Throwable cause)
    {
        super(message, cause);
    }
}
