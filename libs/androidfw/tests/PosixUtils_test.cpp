/*
 * Copyright (C) 2018 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

#include <utility>

#include "androidfw/PosixUtils.h"

#include "TestHelpers.h"

using ::testing::IsNull;
using ::testing::NotNull;

namespace android {
namespace util {

TEST(PosixUtilsTest, AbsolutePathToBinary) {
  const auto result = ExecuteBinary({"/bin/date", "--help"});
  ASSERT_TRUE((bool)result);
  ASSERT_EQ(result.status, 0);
  ASSERT_GE(result.stdout_str.find("usage: date "), 0);
}

TEST(PosixUtilsTest, RelativePathToBinary) {
  const auto result = ExecuteBinary({"date", "--help"});
  ASSERT_TRUE((bool)result);
  ASSERT_EQ(result.status, 0);
  ASSERT_GE(result.stdout_str.find("usage: date "), 0);
}

TEST(PosixUtilsTest, BadParameters) {
  const auto result = ExecuteBinary({"/bin/date", "--this-parameter-is-not-supported"});
  ASSERT_TRUE((bool)result);
  ASSERT_GT(result.status, 0);
}

TEST(PosixUtilsTest, NoSuchBinary) {
  const auto result = ExecuteBinary({"/this/binary/does/not/exist"});
  ASSERT_FALSE((bool)result);
}

} // android
} // util
