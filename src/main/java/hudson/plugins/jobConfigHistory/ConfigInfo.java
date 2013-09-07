package hudson.plugins.jobConfigHistory;

import hudson.model.AbstractItem;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.kohsuke.stapler.export.Exported;
import org.kohsuke.stapler.export.ExportedBean;

/**
 * Holder object for displaying information.
 *
 *
 * @author Stefan Brausch
 */
@ExportedBean(defaultVisibility = 999)
public class ConfigInfo implements ParsedDate {

    /** The display name of the user. */
    private final String user;

    /** The id of the user. */
    private final String userID;

    /** The date of the change. */
    private final String date;

    /** The urlencoded path to the config file of the job. */
    private final String file;

    /** The name of the job or file. */
    private final String job;

    /** One of created, changed, renamed or deleted. */
    private final String operation;

    /** true if this information is for a Hudson job,
     *  as opposed to information for a system configuration file.
     */
    private boolean isJob;

    /**
     * Returns a new ConfigInfo object for a Hudson job.
     *
     * @param item
     *            a project
     * @param file
     *            pointing to {@literal config.xml}
     * @param histDescr
     *            metadata of the change
     * @return a new ConfigInfo object.
     */
    public static ConfigInfo create(final AbstractItem item, final File file, final HistoryDescr histDescr) {
        final String fileUrl = getEncodedUrl(file);
        return new ConfigInfo(
                item.getFullName(),
                fileUrl,
                histDescr.getTimestamp(),
                histDescr.getUser(),
                histDescr.getOperation(),
                histDescr.getUserID(),
                true);
    }

    /**
     * Returns a new ConfigInfo object for a system configuration file.
     * @param name
     *            Name of the configuration entity we are saving.
     * @param file
     *            The file with configuration data.
     * @param histDescr
     *            metadata of the change.
     * @param isJob
     *            whether it is a job's config info or not.
     * @return a new ConfigInfo object.
     */
    public static ConfigInfo create(final String name, final File file, final HistoryDescr histDescr, final boolean isJob) {
        final String encodedURL = getEncodedUrl(file);
        return new ConfigInfo(
                name,
                encodedURL,
                histDescr.getTimestamp(),
                histDescr.getUser(),
                histDescr.getOperation(),
                histDescr.getUserID(),
                isJob);
    }

    /**
     * @param job see {@link ConfigInfo#job}.
     * @param file see {@link ConfigInfo#file}.
     * @param date see {@link ConfigInfo#date}
     * @param user see {@link ConfigInfo#user}
     * @param operation see {@link ConfigInfo#operation}
     * @param userID see {@link ConfigInfo#userID}
     * @param isJob see {@link ConfigInfo#isJob}
     */
    ConfigInfo(String job, String file, String date, String user, String operation, String userID, boolean isJob) {
        this.job = job;
        this.file = file;
        this.date = date;
        this.user = user;
        this.operation = operation;
        this.userID = userID;
        this.isJob = isJob;

    }

    /**
     * Returns the display name of the user.
     *
     * @return display name
     */
    @Exported
    public String getUser() {
        return user;
    }

    /**
     * Returns the id of the user.
     *
     * @return user id
     */
    @Exported
    public String getUserID() {
        return userID;
    }

    /**
     * Returns the date of the change.
     *
     * @return timestamp in the format of {@link JobConfigHistoryConsts#ID_FORMATTER}
     */
    @Exported
    public String getDate() {
        return date;
    }

    /**
     * Returns the URL encoded absolute name of the file.
     *
     * @return URL encoded filename
     */
    @Exported
    public String getFile() {
        return file;
    }

    /**
     * Returns the name of the job.
     *
     * @return name of the job
     */
    @Exported
    public String getJob() {
        return job;
    }

    /**
     * Returns the type of the operation.
     *
     * @return name of the operation
     */
    @Exported
    public String getOperation() {
        return operation;
    }

    /**
     * Returns true if this object represents a Hudson job
     * as opposed to representing a system configuration.
     * @return true if this object stores a Hudson job configuration
     */
    public boolean getIsJob() {
        return isJob;
    }

    @Override
    public String toString() {
        return operation + " on " + file + " @" + date;
    }

    /**
     * Converts give file to encode URL string.
     *
     * @param file to convert
     * @return encoded url
     */
    private static String getEncodedUrl(final File file) {
        final String encodedURL;
        try {
            encodedURL = URLEncoder.encode(file.getAbsolutePath(), "utf-8");
        } catch (UnsupportedEncodingException ex) {
            throw new RuntimeException("Could not encode " + file.getAbsolutePath(), ex);
        }
        return encodedURL;
    }

    /**
     * Returns a {@link Date}.
     *
     * @return The parsed date as a java.util.Date.
     */
    @Override
    public Date parsedDate() {
        try {
            return new SimpleDateFormat(JobConfigHistoryConsts.ID_FORMATTER).parse(getDate());
        } catch (ParseException ex) {
            throw new RuntimeException("Could not parse Date" + getDate(), ex);
        }
    }
}
