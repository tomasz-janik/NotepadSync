/*
 * Copyright 2013 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */
package pl.itomaszjanik.notepadsync;

import android.os.Bundle;
import android.util.Log;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.MetadataChangeSet;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

/**
 * An activity to illustrate how to create a new folder.
 */
public class CreateFolderActivity extends BaseDemoActivity {
    private static final String TAG = "CreateFolderActivity";
    private static String title = "",content = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            title = bundle.getString("title", "");
            content = bundle.getString("content", "");
        }
    }

    @Override
    protected void onDriveClientReady() {
        createFolder();
    }

    // [START create_folder]
    private void createFolder() {
        getDriveResourceClient()
                .getRootFolder()
                .continueWithTask(task -> {
                    DriveFolder parentFolder = task.getResult();
                    MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                                                          .setTitle("NotepadSync")
                                                          .setMimeType(DriveFolder.MIME_TYPE)
                                                          .setStarred(true)
                                                          .build();
                    return getDriveResourceClient().createFolder(parentFolder, changeSet);
                })
                .addOnSuccessListener(this,
                        driveFolder -> {
                            createFileInFolder(driveFolder.getDriveId().asDriveFolder());
                            finish();
                        })
                .addOnFailureListener(this, e -> {
                    Log.e(TAG, "Unable to create file", e);
                    showMessage(getString(R.string.file_create_error));
                    finish();
                });
    }
    // [END create_folder]

    private void createFileInFolder(final DriveFolder parent) {
        getDriveResourceClient()
                .createContents()
                .continueWithTask(task -> {
                    DriveContents contents = task.getResult();
                    OutputStream outputStream = contents.getOutputStream();
                    try (Writer writer = new OutputStreamWriter(outputStream)) {
                        writer.write(content);
                    }

                    MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                            .setTitle(title + ".txt")
                            .setMimeType("text/plain")
                            .setStarred(true)
                            .build();

                    return getDriveResourceClient().createFile(parent, changeSet, contents);
                })
                .addOnFailureListener(this, e -> {
                    Log.e(TAG, "Unable to create file", e);
                    showMessage(getString(R.string.file_create_error));
                });
    }
}
