package org.thoughtcrime.securesms.migrations;

import androidx.annotation.NonNull;

import com.annimon.stream.Stream;

import org.thoughtcrime.securesms.dependencies.ApplicationDependencies;
import org.thoughtcrime.securesms.jobmanager.Data;
import org.thoughtcrime.securesms.jobmanager.Job;
import org.thoughtcrime.securesms.jobmanager.JobManager;
import org.thoughtcrime.securesms.jobs.MultiDeviceKeysUpdateJob;
import org.thoughtcrime.securesms.jobs.StickerPackDownloadJob;
import org.thoughtcrime.securesms.jobs.StorageSyncJob;
import org.thoughtcrime.securesms.logging.Log;
import org.thoughtcrime.securesms.stickers.BlessedPacks;
import org.thoughtcrime.securesms.util.TextSecurePreferences;

import java.util.Arrays;
import java.util.List;

public class StorageServiceMigrationJob extends MigrationJob {

  private static final String TAG = Log.tag(StorageServiceMigrationJob.class);

  public static final String KEY = "StorageServiceMigrationJob";

  StorageServiceMigrationJob() {
    this(new Parameters.Builder().build());
  }

  private StorageServiceMigrationJob(@NonNull Parameters parameters) {
    super(parameters);
  }

  @Override
  public boolean isUiBlocking() {
    return false;
  }

  @Override
  public @NonNull String getFactoryKey() {
    return KEY;
  }

  @Override
  public void performMigration() {
    JobManager jobManager = ApplicationDependencies.getJobManager();

    if (TextSecurePreferences.isMultiDevice(context)) {
      Log.i(TAG, "Multi-device.");
      jobManager.startChain(new MultiDeviceKeysUpdateJob())
                .then(new StorageSyncJob())
                .enqueue();
    } else {
      Log.i(TAG, "Single-device.");
      jobManager.add(new StorageSyncJob());
    }
  }

  @Override
  boolean shouldRetry(@NonNull Exception e) {
    return false;
  }

  public static class Factory implements Job.Factory<StorageServiceMigrationJob> {
    @Override
    public @NonNull StorageServiceMigrationJob create(@NonNull Parameters parameters, @NonNull Data data) {
      return new StorageServiceMigrationJob(parameters);
    }
  }
}
