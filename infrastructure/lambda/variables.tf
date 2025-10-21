variable "account_id" {}

variable "environment_name" {}

variable "app_name" {
  description = "Name of the application"
  type        = string
  default     = "lambda-springboot-sample"
}

variable "environment" {
  description = "Environment name"
  type        = string
  default     = "dev"
}

variable "default_ttl" {}
variable "revision_hash" {
  default = ""
}
variable "cloud_front_arn" {}
