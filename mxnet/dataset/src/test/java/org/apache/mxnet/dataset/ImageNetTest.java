/*
 * Copyright 2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"). You may not use this file except in compliance
 * with the License. A copy of the License is located at
 *
 * http://aws.amazon.com/apache2.0/
 *
 * or in the "license" file accompanying this file. This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES
 * OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions
 * and limitations under the License.
 */
package org.apache.mxnet.dataset;

import java.io.IOException;
import org.testng.Assert;
import software.amazon.ai.Model;
import software.amazon.ai.repository.Repository;
import software.amazon.ai.training.DefaultTrainingConfig;
import software.amazon.ai.training.Trainer;
import software.amazon.ai.training.TrainingConfig;
import software.amazon.ai.training.dataset.Batch;
import software.amazon.ai.training.dataset.Dataset.Usage;
import software.amazon.ai.training.initializer.Initializer;

public class ImageNetTest {

    // ImageNet requires running manual download so can't be automatically tested
    // @Test
    public void testImageNetLocal() throws IOException {
        Repository repository =
                Repository.newInstance(
                        "test", System.getProperty("user.home") + "/Desktop/testImagenet");
        ImageNet imagenet =
                new ImageNet.Builder()
                        .setUsage(Usage.VALIDATION)
                        .optRepository(repository)
                        .setSampling(32)
                        .build();
        imagenet.prepare();

        try (Model model = Model.newInstance()) {
            TrainingConfig config = new DefaultTrainingConfig(Initializer.ONES, false);
            try (Trainer trainer = model.newTrainer(config)) {
                for (Batch batch : trainer.iterateDataset(imagenet)) {
                    Assert.assertEquals(batch.getData().size(), 1);
                    Assert.assertEquals(batch.getLabels().size(), 1);
                    batch.close();
                }
            }
        }
    }
}
